//
// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to You under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package streampipes

import (
	"errors"
	"streampipes-client-go/streampipes/api"
	"streampipes-client-go/streampipes/config"
)

/*
 This is the central point of contact with StreamPipes and provides all the functionalities to interact with it.
 The client provides so-called "API", each of which refers to the endpoint of the StreamPipes API.
 e.g. `DataLakeMeasureApi` provides the actual methods to interact with StreamPipes API.
*/

type StreamPipesClient struct {
	Config config.StreamPipesClientConnectionConfig
}

func NewStreamPipesClient(config config.StreamPipesClientConnectionConfig) (*StreamPipesClient, error) {

	//NewStreamPipesClient returns an instance of * StreamPipesClient
	//Temporarily does not support HTTPS connections, nor does it support connecting to port 443

	if !config.HttpsDisabled || config.StreamPipesPort == "443" {
		return &StreamPipesClient{}, errors.New(
			"Invalid configuration passed! The given client configuration has " +
				"`https_disabled` set to `True` and `port` set to `443`.\n " +
				"If you want to connect to port 443, use `https_disabled=False` or " +
				"alternatively connect to port .")
	}

	return &StreamPipesClient{
		config,
	}, nil

}

func (s *StreamPipesClient) DataLakeMeasureApi() *api.DataLakeMeasureApi {

	return api.NewDataLakeMeasureApi(s.Config)

}
