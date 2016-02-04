/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fzi.cep.sepa.actions.samples.number;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.actions.samples.util.ActionUtils;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.SO;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eberle
 */
public class NumberController extends ActionController {

        @Override
        public boolean isVisualizable() {
                return false;
        }

        @Override
        public String getHtml(SecInvocation graph) {
                String newUrl = createWebsocketUri(graph);
                String inputTopic = extractTopic(graph);
                String propertyName = SepaUtils.getMappingPropertyName(graph, "mapping");
                String color = SepaUtils.getFreeTextStaticPropertyValue(graph, "color");
                NumberParameters numberParameters = new NumberParameters(inputTopic, newUrl, propertyName, color);
                return new NumberGenerator(numberParameters).generateHtml();
        }

        @Override
        public SecDescription declareModel() {
                SecDescription sec = new SecDescription("number", "Number", "Displays a colored circle with value", "");
                //sec.setIconUrl(ActionConfig.iconBaseUrl + "/---.png");
                List<String> domains = new ArrayList<String>();
                domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
                domains.add(Domain.DOMAIN_PROASENSE.toString());

                List<EventProperty> eventProperties = new ArrayList<EventProperty>();
                EventProperty e1 = new EventPropertyPrimitive(Utils.createURI(SO.Number));
                eventProperties.add(e1);

                EventSchema schema1 = new EventSchema();
                schema1.setEventProperties(eventProperties);

                EventStream stream1 = new EventStream();
                stream1.setEventSchema(schema1);
                stream1.setUri(ActionConfig.serverUrl + "/" + Utils.getRandomString());
                sec.addEventStream(stream1);

                List<StaticProperty> staticProperties = new ArrayList();
                staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping", "Property Mapping", ""));
                staticProperties.add(new FreeTextStaticProperty("color", "Color of the cirlce", ""));
                sec.setStaticProperties(staticProperties);

                sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());
                return sec;
        }

        @Override
        public Response invokeRuntime(SecInvocation invocationGraph) {
                return null;
        }

        @Override
        public Response detachRuntime(String pipelineId) {
                return null;
        }

}
