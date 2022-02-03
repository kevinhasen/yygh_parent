package com.yee.yygh.hosp.testmongo.test;


import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.testmongo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: UserTest
 * Description:
 * date: 2021/12/27 20:01
 * mongoDb测试类
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/mongoOne")
public class UserTest {

    //注入模板
    @Autowired
    private MongoTemplate mongoTemplatel;
    //创建用户
    @GetMapping("/createUser")
    public Result createUser(){
        User user = new User();
        user.setAge(20);
        user.setName("test");
        user.setEmail("4932200@qq.com");
        mongoTemplatel.insert(user);
        return Result.ok();
    }

    //查询所有
    @GetMapping("/findAll")
    public Result findAll(){
        List<User> list = mongoTemplatel.findAll(User.class);
        return Result.ok().data("list",list);
    }

    //根据id查询
    @GetMapping("/findId")
    public Result findId(){
        String id = "61c9b7adc372537ad287d512";
        User user = mongoTemplatel.findById(id, User.class);
        return Result.ok().data("user",user);
    }

    //条件查询
    @GetMapping("/findUser")
    public Result findUserList() {
        //要查询的条件
        Query query = new Query(Criteria.where("name").is("test").and("age").is(20));
        //根据查询条件查询
        List<User> list = mongoTemplatel.find(query, User.class);
        return Result.ok().data("list",list);
    }

    //删除操作
    @GetMapping("/delete")
    public Result delete(Long id) {
        Query query = new Query(Criteria.where("_id").is("61c9b7adc372537ad287d512"));
        mongoTemplatel.remove(query, User.class);
        return Result.ok();
    }
}
