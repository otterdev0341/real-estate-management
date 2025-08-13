package common.service.declare.fileAssetManagement;

import common.domain.entity.FileDetail;

import java.util.Set;

public interface HasFileDetails {
    Set<FileDetail> getFileDetails();
}
