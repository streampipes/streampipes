#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
Specific implementation of the StreamPipes API's data lake measure endpoints.
This endpoint allows to consume data stored in StreamPipes' data lake
"""
from datetime import datetime
from typing import Any, Dict, Literal, Optional, Tuple, Type

from pydantic import BaseModel, Extra, Field, StrictInt, ValidationError, validator
from streampipes.endpoint.endpoint import APIEndpoint
from streampipes.model.container import DataLakeMeasures
from streampipes.model.container.resource_container import ResourceContainer
from streampipes.model.resource import DataLakeSeries

__all__ = [
    "DataLakeMeasureEndpoint",
]


class StreamPipesQueryValidationError(Exception):
    """A custom exception to be raised when the validation of query parameter
    causes an error.

    Parameters
    ----------
    validation_error: ValidationError
        The validation error thrown by Pydantic during parsing.
    """


class MeasurementGetQueryConfig(BaseModel):
    """Config class describing the parameters of the GET endpoint for measurements.

    This config class is used to validate the provided query parameters for the GET endpoint of measurements.
    Additionally, it takes care of the conversion to a proper HTTP query string.
    Thereby, parameter names are adapted to the naming of the StreamPipes API, for which Pydantic aliases are used.

    Attributes
    ----------
    columns: Optional[str]
        A comma separated list of column names (e.g., `time,value`)<br>
        If provided, the returned data only consists of the given columns.
    end_date: Optional[datetime]
        Restricts queried data to be younger than the specified time.
    limit: Optional[int]
        Amount of records returned at maximum (default: `1000`) <br>
        This needs to at least `1`
    offset: Optional[int]
        Offset to be applied to returned data <br>
        This needs to at least `0`
    order: Optional[str]
        Ordering of query results <br>
        Allowed values: `ASC` and `DESC` (default: `ASC`)
    page_no: Optional[int]
        Page number used for paging operation <br>
        This needs to at least `1`
    start_date: Optional[datetime]
        Restricts queried data to be older than the specified time

    """

    _regex_comma_separated_string = r"^[0-9a-zA-Z\_]+(,[0-9a-zA-Z\_]+)*$"

    class Config:
        """Pydantic Config class"""

        extra = Extra.forbid
        allow_population_by_field_name = True

    columns: Optional[str] = Field(regex=_regex_comma_separated_string)
    end_date: Optional[StrictInt] = Field(alias="endDate")
    limit: Optional[int] = Field(ge=1, default=1000)
    offset: Optional[int] = Field(ge=0)
    order: Optional[Literal["ASC", "DESC"]]
    page_no: Optional[int] = Field(alias="page", ge=1)
    start_date: Optional[StrictInt] = Field(alias="startDate")

    @validator("end_date", "start_date", pre=True)
    @classmethod
    def _convert_datetime(cls, dt: datetime) -> int:
        """Pydantic validator to convert datetime object to unix timestamp.

        The StreamPipes API expects datetime related parameters to be passed as unix timestamp.
        For the sake of convenience we expect datetime objects to be passed for these values.
        This requires us to convert the provided datetime objects in unix timestamp representation

        Parameters
        ----------
        dt: datetime
            The datetime value to be passed as query parameter


        Raises
        ------
        StreamPipesQueryValidationError
            In case `start_date` or `end_date` is not passed as a datetime object
        ValueError
            In case the transformation of the datetime object did not work

        Returns
        -------
        unix_timestamp: int
            unix timestamp of the given timestamp

        """

        if not isinstance(dt, datetime) or dt is None:
            raise StreamPipesQueryValidationError(
                f"The passed value for either `start_date` or `end_date` " f"is not a datetime object: '{dt}'."
            )
        try:
            unix_timestamp = int(datetime.timestamp(dt) * 1000)
            return unix_timestamp
        except ValueError as ve:  # pragma: no cover
            raise ValueError(
                "Your datetime object is off, it could not be parsed"
                "This should not occur, but unfortunately did.\n"
                "Therefore, it would be great if you could report this problem as an issue at "
                "github.com/apache/streampipes.\n"
            ) from ve

    def build_query_string(self) -> str:
        """Builds a HTTP query string for the config.

        This method returns an HTTP query string for the invoking config.
        It follows the following structure `?param1=value1&param2=value2...`.
        This query string is not an entire URL, instead it needs to appended to an API path.

        Returns
        -------
        query_param_string: str
            HTTP query params string (`?param1=value1&param2=value2...`)
        """

        # create dictionary representation of the config that meets the following expectations:
        # - query parameter should comply to the parameter names of the StreamPipes API (`by_alias`)
        # - query params should only be present if they are different from None (`exclude_none`)
        query_param_dict = self.dict(by_alias=True, exclude_none=True)

        # create query string that complies to HTTP syntax (?param1=value1&param2=value2&...)
        query_param_string = f"?{'&'.join([f'{k}={v}' for k, v in query_param_dict.items()])}"

        return query_param_string


class DataLakeMeasureEndpoint(APIEndpoint):
    """Implementation of the DataLakeMeasure endpoint.

    This endpoint provides an interfact to all data stored in the StreamPipes data lake.

    Consequently, it allows uerying metadata about available data sets (see `all()` method).
    The metadata is returned as an instance of `model.container.DataLakeMeasures`.

    In addition, the endpoint provides direct access to the data stored in the data laka by querying a
    specific data lake measure using the `get()` method.

    Parameters
    ----------
    parent_client: StreamPipesClient
        The instance of `client.StreamPipesClient` the endpoint is attached to.

    Examples
    --------

    >>> from streampipes.client import StreamPipesClient
    >>> from streampipes.client.config import StreamPipesClientConfig
    >>> from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

    >>> client_config = StreamPipesClientConfig(
    ...     credential_provider=StreamPipesApiKeyCredentials(username="test-user", api_key="api-key"),
    ...     host_address="localhost",
    ...     port=8082,
    ...     https_disabled=True
    ... )

    >>> client = StreamPipesClient.create(client_config=client_config)

    >>> data_lake_measures = client.dataLakeMeasureApi.all()

    >>> len(data_lake_measures)
    5
    """

    @staticmethod
    def _validate_query_params(query_params: Dict[str, Any]) -> MeasurementGetQueryConfig:
        """Validates given query params.

        Validates the given query parameters via the
        [MeasurementGetQueryConfig][streampipes.endpoint.api.data_lake_measure.MeasurementGetQueryConfig].

        Raises
        ------
        StreamPipesQueryValidationError
            In case the query parameters are not provided correctly

        Returns
        -------
        config: MeasurementGetQueryConfig
            validated config that can be used to construct the query
        """
        try:
            config = MeasurementGetQueryConfig.parse_obj(query_params)
        except ValidationError as ve:
            raise StreamPipesQueryValidationError(
                f"\nOops, there seems to be a problem with your provided query options. "
                f"Some of them are not provided as expected. Please see the detailed output below:\n\n"
                f"Validation error log: {ve.json()}\n\n"
                f"In case you assess your query configuration to be correct feel free to file us an issue via "
                f"github.com/apache/streampipes.\n"
                f"Please don't forget to include the following validation log from above."
            )

        return config

    @property
    def _resource_cls(self) -> Type[DataLakeSeries]:
        """
        Additional reference to resource class.
        This endpoint deviates from the desired relationship
        that the resource class of the resource container is
        the return type of the get endpoint.
        Therefore, this is only a temporary implementation and will be removed soon.
        """
        return DataLakeSeries

    @property
    def _container_cls(self) -> Type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.


        Returns
        -------
        `model.container.DataLakeMeasures`
        """
        return DataLakeMeasures

    @property
    def _relative_api_path(self) -> Tuple[str, ...]:
        """Defines the relative api path to the DataLakeMeasurement endpoint.
        Each path within the URL is defined as an own string.

        Returns
        -------
        A tuple of strings of which every represents a path value of the endpoint's API URL.
        """

        return "api", "v4", "datalake", "measurements"

    def get(self, identifier: str, **kwargs: Optional[Dict[str, Any]]) -> DataLakeSeries:
        """Queries the specified data lake measure from the API.

        By default, the maximum number of returned records is 1000.
        This behaviour can be influences by passing the parameter `limit` with a different value
        (see [MeasurementGetQueryConfig][streampipes.endpoint.api.data_lake_measure.MeasurementGetQueryConfig]).

        Parameters
        ----------
        identifier: str
            The identifier of the data lake measure to be queried.
        **kwargs: Dict[str, Any]
            keyword arguments can be used to provide additional query parameters.
            The available query parameters are defined by the
            [MeasurementGetQueryConfig][streampipes.endpoint.api.data_lake_measure.MeasurementGetQueryConfig].

        Returns
        -------
        measurement: DataLakeMeasures
            the specified data lake measure
        """

        # bild base URL for resource
        url = f"{self.build_url()}/{identifier}"

        # extend base URL by query parameters
        measurement_get_config = self._validate_query_params(query_params=kwargs)
        url += measurement_get_config.build_query_string()

        response = self._make_request(request_method=self._parent_client.request_session.get, url=url)
        return self._resource_cls.from_json(json_string=response.text)
