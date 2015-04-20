package lab.s2jh.module.sys.dao;

import java.util.List;

import lab.s2jh.core.dao.jpa.BaseDao;
import lab.s2jh.module.sys.entity.AttachmentFile;

import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentFileDao extends BaseDao<AttachmentFile, String> {
    List<AttachmentFile> findByEntityClassNameAndEntityIdAndEntityFileCategory(String entityClassName, String entityId,
            String entityFileCategory);

    List<AttachmentFile> findByEntityClassNameAndEntityId(String entityClassName, String entityId);

}
