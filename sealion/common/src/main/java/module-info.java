module common {
    requires com.fasterxml.jackson.annotation;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires jakarta.ws.rs;
    requires static lombok;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires io.quarkus.security.api;
    requires smallrye.jwt.build;
    requires Either.java;
    requires org.mapstruct;
    requires org.hibernate.orm.core;

    exports common.domain.entity;
    exports common.domain.dto.base;
    exports common.domain.dto.query;
    exports common.domain.dto.auth;
    exports common.domain.mapper;
    exports common.domain.dto.user;
    exports common.errorStructure;
    exports common.implementation.antonation.validator;
    exports common.response;
    exports common.controller.base;
    exports common.service.implementation;
}