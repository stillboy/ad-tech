package com.deali.adtech.infrastructure.repository;

import com.deali.adtech.presentation.dto.ResponseCreative;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.schema.JsonSchemaObject;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.springframework.data.mongodb.core.schema.JsonSchemaObject.*;

@RequiredArgsConstructor
@Repository
public class AdvertisementDocumentMongoRepository
        implements AdvertisementDocumentRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<ResponseCreative> searchTop10Advertisement() {
        //TODO::min-max 정규화 (x-min)/(max-min)

        HashMap<String, Number> map = searchMinMaxWinningBidAndModifiedAt();

        Integer maxBid = (Integer)map.get("maxBid");
        Integer minBid = (Integer)map.get("minBid");
        Long maxDate = (Long)map.get("maxDate");
        Long minDate = (Long)map.get("minDate");

        ProjectionOperation projectionOperation = Aggregation
                .project("_id","title","winningBid","modifiedAt","expiryDate",
                        "advertisementId","imagePath")
                .and(add(
                        multiply(0.4,
                                divide(subtract(convert("modifiedAt", Type.INT_64), minDate),
                                        maxDate-minDate)
                                ),
                        multiply(0.6, divide(subtract("winningBid", minBid), maxBid-minBid))
                ))
                .as("score");


        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "score");

        LimitOperation limitOperation = Aggregation.limit(10);

        Aggregation aggregation = Aggregation.newAggregation(projectionOperation, sortOperation,
                limitOperation);

        AggregationResults<ResponseCreative> results = mongoTemplate.aggregate(aggregation, "advertisement",
                ResponseCreative.class);

        return results.getMappedResults();
    }

    @Override
    public HashMap<String,Number> searchMinMaxWinningBidAndModifiedAt() {
       GroupOperation groupOperation = Aggregation.group()
               .max("winningBid")
               .as("maxBid")
               .min("winningBid")
               .as("minBid")
               .max("modifiedAt")
               .as("maxDate")
               .min("modifiedAt")
               .as("minDate");

       Aggregation aggregation = Aggregation.newAggregation(groupOperation);

       AggregationResults<HashMap> results =
               mongoTemplate.aggregate(aggregation, "advertisement", HashMap.class);

       HashMap result = results.getMappedResults().get(0);

       Date maxDate = (Date)result.get("maxDate");
       Date minDate = (Date)result.get("minDate");

       result.replace("maxDate", maxDate.getTime());
       result.replace("minDate", minDate.getTime());

       return result;
    }

    private AggregationExpression add(AggregationExpression left, AggregationExpression right) {
        return ArithmeticOperators.Add.valueOf(left).add(right);
    }

    private AggregationExpression multiply(AggregationExpression left, AggregationExpression right) {
        return ArithmeticOperators.Multiply.valueOf(left).multiplyBy(right);
    }

    private AggregationExpression multiply(Number left, AggregationExpression right) {
        return ArithmeticOperators.Multiply.valueOf(left).multiplyBy(right);
    }

    private AggregationExpression divide(AggregationExpression left, AggregationExpression right) {
        return ArithmeticOperators.Divide.valueOf(left).divideBy(right);
    }

    private AggregationExpression divide(AggregationExpression left, Number right) {
        return ArithmeticOperators.Divide.valueOf(left).divideBy(right);
    }

    private AggregationExpression subtract(AggregationExpression left, AggregationExpression right) {
        return ArithmeticOperators.Subtract.valueOf(left).subtract(right);
    }

    private AggregationExpression subtract(AggregationExpression left, Number right) {
        return ArithmeticOperators.Subtract.valueOf(left).subtract(right);
    }

    private AggregationExpression subtract(String left, Number right) {
        return ArithmeticOperators.Subtract.valueOf(left).subtract(right);
    }

    private AggregationExpression convert(String fieldName, Type type) {
        return ConvertOperators.Convert.convertValueOf(fieldName).to(type);
    }
}
