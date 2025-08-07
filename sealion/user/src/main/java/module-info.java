module user {
    requires common;
    requires Either.java;
    requires jakarta.cdi;
    requires quarkus.hibernate.orm.panache;
    requires static lombok;
    requires jakarta.validation;
    requires com.fasterxml.jackson.annotation;
    requires org.mapstruct;
    requires jbcrypt;
    requires jakarta.ws.rs;
    requires org.eclipse.microprofile.openapi;
    requires jakarta.transaction;


}