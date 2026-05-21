package com.flamingo.qa.api.restapi.filter;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(LoggingFilter.class);
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        Prettifier prettifier = new Prettifier();
        String requestBody = prettifier.getPrettifiedBodyIfPossible(requestSpec);
        LOGGER.info("\n--- REQUEST ---\n" +
                        "Method:  {}\n" +
                        "URL:     {}\n" +
                        "Body:    \n{}\n",
                requestSpec.getMethod(), requestSpec.getURI(), requestBody);
        Response response = ctx.next(requestSpec, responseSpec);
        String responseBody = prettifier.getPrettifiedBodyIfPossible(response, response.body());
        LOGGER.info("\n--- RESPONSE ---\n" +
                        "Status:  {}\n" +
                        "Body:    \n{}\n",
                response.getStatusCode(), responseBody);
        return response;
    }
}