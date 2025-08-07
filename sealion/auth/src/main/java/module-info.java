module auth {
    exports auth.service.implementation;
    exports auth.service.declare;



    requires Either.java;
    requires jakarta.validation;
    requires static lombok;
    requires common;
    requires jakarta.cdi;
    requires io.quarkus.security.api;
    requires smallrye.jwt.build;
    requires jbcrypt;
    requires jakarta.ws.rs;
    requires org.eclipse.microprofile.openapi;
    requires quarkus.hibernate.orm.panache;
    requires jakarta.transaction;
    requires jakarta.persistence;
    requires quarkus.core;
    requires org.hibernate.orm.core;


}