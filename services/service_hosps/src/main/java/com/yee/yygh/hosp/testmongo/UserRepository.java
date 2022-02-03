package com.yee.yygh.hosp.testmongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * ClassName: UserRepository
 * Description:
 * date: 2021/12/27 20:59
 * 这边可以设置方法规范查询
 * @author Yee
 * @since JDK 1.8
 */
public interface UserRepository extends MongoRepository<User,String> {
    List<User> getByNameAndAge(String name, int age);

    List<User> getByNameLike(String name);

}
