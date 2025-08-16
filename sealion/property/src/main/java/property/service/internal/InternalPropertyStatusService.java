package property.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyStatus;
import common.errorStructure.ServiceError;
import property.domain.dto.propertyStatus.ReqCreatePropertyStatusDto;
import property.domain.dto.propertyStatus.ReqUpdatePropertyStatusDto;

import java.util.List;
import java.util.UUID;

public interface InternalPropertyStatusService {

    Either<ServiceError, PropertyStatus> createNewPropertyStatus(ReqCreatePropertyStatusDto reqCreatePropertyStatusDto, UUID userId);

    Either<ServiceError, PropertyStatus> updatePropertyStatus(ReqUpdatePropertyStatusDto reqUpdatePropertyStatusDto, UUID propertyStatusId, UUID userId);

    Either<ServiceError, Boolean> deletePropertyStatus(UUID propertyStatusId, UUID userId);

    Either<ServiceError, List<PropertyStatus>> findAllPropertyStatues(UUID userId, BaseQuery query);


}
