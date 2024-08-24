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

package model

type ValueSpecification struct {
	ElementID string  `json:"elementId,omitempty"`
	MinValue  int     `json:"minValue,omitempty"`
	MaxValue  int     `json:"maxValue,omitempty"`
	Step      float64 `json:"step,omitempty"`
}

type EventProperty struct {
	ElementID          string   `json:"elementId"`
	Label              string   `json:"label,omitempty"`
	Description        string   `json:"description,omitempty"`
	RuntimeName        string   `json:"runtimeName,omitempty"`
	Required           bool     `json:"required,omitempty"`
	DomainProperties   []string `json:"domainProperties,omitempty"`
	PropertyScope      string   `json:"propertyScope,omitempty"`
	Index              int32    `json:"index"`
	RuntimeID          string   `json:"runtimeId,omitempty"`
	AdditionalMetadata map[string]interface{}
	RuntimeType        string             `json:"runtimeType"`
	MeasurementUnit    string             `json:"measurementUnit,omitempty"`
	ValueSpecification ValueSpecification `json:"valueSpecification,omitempty"`
}

type EventProperties struct {
	ElementID          string            `json:"elementId"`
	Label              string            `json:"label"`
	Description        string            `json:"description"`
	RuntimeName        string            `json:"runtimeName"`
	Required           bool              `json:"required"`
	DomainProperties   []string          `json:"domainProperties"`
	PropertyScope      string            `json:"propertyScope"`
	Index              int32             `json:"index"`
	RuntimeID          string            `json:"runtimeId"`
	RuntimeType        string            `json:"runtimeType,omitempty"`
	MeasurementUnit    string            `json:"measurementUnit,omitempty"`
	ValueSpecification string            `json:"valueSpecification,omitempty"`
	InEventProperties  []EventProperties `json:"eventProperties,omitempty"`
	InEventProperty    EventProperty     `json:"eventProperty,omitempty"`
}

type EventSchema struct {
	EventProperties []EventProperties `json:"eventProperties"`
}

type DataSeries struct {
	Total   int               `json:"total"`
	Rows    [][]string        `json:"rows"`
	Headers []string          `json:"http_headers"`
	Tags    map[string]string `json:"tags"`
}

type ResponseMessage struct {
	Success       bool           `json:"success"`
	ElementName   string         `json:"elementName"`
	Notifications []Notification `json:"notifications"`
}

type Notification struct {
	Title                 string      `json:"title"`
	Description           interface{} `json:"description"`
	AdditionalInformation string      `json:"additionalInformation"`
}

type StaticPropertyType string

const (
	AnyStaticProperty                        StaticPropertyType = "AnyStaticProperty"
	CodeInputStaticProperty                  StaticPropertyType = "CodeInputStaticProperty"
	CollectionStaticProperty                 StaticPropertyType = "CollectionStaticProperty"
	ColorPickerStaticProperty                StaticPropertyType = "ColorPickerStaticProperty"
	DomainStaticProperty                     StaticPropertyType = "DomainStaticProperty"
	FreeTextStaticProperty                   StaticPropertyType = "FreeTextStaticProperty"
	FileStaticProperty                       StaticPropertyType = "FileStaticProperty"
	MappingPropertyUnary                     StaticPropertyType = "MappingPropertyUnary"
	MappingPropertyNary                      StaticPropertyType = "MappingPropertyNary"
	MatchingStaticProperty                   StaticPropertyType = "MatchingStaticProperty"
	OneOfStaticProperty                      StaticPropertyType = "OneOfStaticProperty"
	RuntimeResolvableAnyStaticProperty       StaticPropertyType = "RuntimeResolvableAnyStaticProperty"
	RuntimeResolvableGroupStaticProperty     StaticPropertyType = "RuntimeResolvableGroupStaticProperty"
	RuntimeResolvableOneOfStaticProperty     StaticPropertyType = "RuntimeResolvableOneOfStaticProperty"
	RuntimeResolvableTreeInputStaticProperty StaticPropertyType = "RuntimeResolvableTreeInputStaticProperty"
	StaticPropertyGroup                      StaticPropertyType = "StaticPropertyGroup"
	StaticPropertyAlternatives               StaticPropertyType = "StaticPropertyAlternatives"
	StaticPropertyAlternative                StaticPropertyType = "StaticPropertyAlternative"
	SecretStaticProperty                     StaticPropertyType = "SecretStaticProperty"
	SlideToggleStaticProperty                StaticPropertyType = "SlideToggleStaticProperty"
)

type StaticProperty struct {
	Optional           bool               `json:"optional,omitempty"`
	StaticPropertyType StaticPropertyType `json:"staticPropertyType"`
	Index              int32              `json:"index"`
	Label              string             `json:"label"`
	Description        string             `json:"description"`
	InternalName       string             `json:"internalName"`
	Predefined         bool               `json:"predefined"`
	Class              string             `json:"@class"`
}

type SpDataStream struct {
	ElementId              string         `json:"elementId"`
	Dom                    string         `json:"dom"`
	ConnectedTo            []string       `json:"connectedTo"`
	Name                   string         `json:"name"`
	Description            string         `json:"description"`
	IconUrl                string         `json:"iconUrl"`
	AppId                  string         `json:"appId"`
	IncludesAssets         bool           `json:"includesAssets"`
	IncludesLocales        bool           `json:"includesLocales"`
	IncludedAssets         []string       `json:"includedAssets"`
	IncludedLocales        []string       `json:"includedLocales"`
	InternallyManaged      bool           `json:"internallyManaged"`
	EventGrounding         EventGrounding `json:"eventGrounding"`
	EventSchema            EventSchema    `json:"eventSchema"`
	Category               []string       `json:"category"`
	Index                  int32          `json:"index"`
	CorrespondingAdapterId string         `json:"correspondingAdapterId"`
	Rev                    string         `json:"_rev"`
}

type EventGrounding struct {
	TransportProtocols []TransportProtocol `json:"transportProtocols"`
	TransportFormats   []TransportFormat   `json:"transportFormats"`
}

type TransportProtocol struct {
	ElementId       string          `json:"elementId"`
	BrokerHostname  string          `json:"brokerHostname"`
	TopicDefinition TopicDefinition `json:"topicDefinition"`
	Class           string          `json:"@class,omitempty"`
	Port            int             `json:"port"`
}

type TopicDefinition struct {
	ActualTopicName string `json:"actualTopicName"`
	Class           string `json:"@class"`
}

type TransportFormat struct {
	RdfType []string `json:"rdfType"`
}
