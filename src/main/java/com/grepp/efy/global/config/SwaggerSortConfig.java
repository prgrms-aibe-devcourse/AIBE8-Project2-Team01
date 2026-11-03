package com.grepp.efy.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class SwaggerSortConfig extends SwaggerIndexPageTransformer {
    public SwaggerSortConfig(SwaggerUiConfigProperties config, SwaggerUiOAuthProperties oauth,
                             SwaggerWelcomeCommon welcome, ObjectMapperProvider objectMapperProvider) {
        super(config, oauth, welcome, objectMapperProvider);
    }

    // 설정 파일의 문자열이 아닌 실제 JavaScript 함수로 전달해야 사용자 지정 정렬이 동작한다.
    @Override
    protected String addParameters(String html, SwaggerUiConfigParameters parameters) throws JsonProcessingException {
        String page = super.addParameters(html, parameters);
        return page.replace("window.ui = SwaggerUIBundle({", """
                window.ui = SwaggerUIBundle({
                  operationsSorter: (a, b) => {
                    const methods = ["get", "post", "put", "patch", "delete", "options", "head", "trace"];
                    return methods.indexOf(a.get("method")) - methods.indexOf(b.get("method"));
                  },
                """);
    }
}
