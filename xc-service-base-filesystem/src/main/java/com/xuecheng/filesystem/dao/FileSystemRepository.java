package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName FileSystemRepository
 * @Author 邓元粮
 * @Date 2019/6/7 7:08
 * @Version 1.0
 **/
public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
