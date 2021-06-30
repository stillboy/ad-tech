package com.deali.adtech;

import com.deali.adtech.presentation.dto.RequestCreateAdvertisement;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public class RequestCreateAdvertisementDeserializer extends JsonDeserializer<RequestCreateAdvertisement> {

    @Override
    public RequestCreateAdvertisement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = p.getCodec();
        JsonNode jsonNode = objectCodec.readTree(p);

        ObjectMapper objectMapper = new ObjectMapper();

        LocalDateTime exposureDate = objectMapper
                .convertValue(jsonNode.get("exposureDate"), LocalDateTime.class);


        RequestCreateAdvertisement request = new RequestCreateAdvertisement();
        request.setWinningBid(jsonNode.get("winningBid").asInt());
        request.setTitle(jsonNode.get("title").asText());
        //request.setExpiryDate();
        //request.setExposureDate();

        return request;
    }
}
