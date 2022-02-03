package com.yee.yygh.hosp.testmongo.test;

import com.yee.yygh.common.result.Result;
import com.yee.yygh.hosp.testmongo.User;
import com.yee.yygh.hosp.testmongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * ClassName: UserTestTwo
 * Description:
 * date: 2021/12/27 21:00
 *
 * @author Yee
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/mongoTwo")
public class UserTestTwo {

    @Autowired
    private UserRepository userRepository;

    //添加
    @GetMapping("/create")
    public Result createUser() {
        User user = new User();
        user.setAge(20);
        user.setName("张三");
        user.setEmail("4932200@qq.com");
       userRepository.insert(user);
        return Result.ok();
    }

    //查询所有
    @GetMapping("/findAll")
    public Result findUser() {
        List<User> list = userRepository.findAll();
        return Result.ok().data("list",list);
    }

    //id查询
    @GetMapping("/findId")
    public Result getById() {
        String id = "61c9bb108dcbb561b3c3c124";
        User user = userRepository.findById(id).get();
        return Result.ok().data("user",user);
    }

    //条件查询
    @GetMapping("/findQuery")
    public Result findUserList() {
        //设置查询条件
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        Example<User> example = Example.of(user);
        List<User> list = userRepository.findAll(example);
        return Result.ok().data("list",list);
    }

    //先查询后修改
    @GetMapping("/update")
    public Result updateUser() {
        User user = userRepository.findById("61c9bb108dcbb561b3c3c124").get();
        user.setName("li四");
        user.setAge(20);
        userRepository.save(user);
        return Result.ok();
    }

    //删除
    @GetMapping("/delete")
    public Result delete() {
    userRepository.deleteById("61c9bb108dcbb561b3c3c124");
    return Result.ok();
    }


    //以下使用了方法规范查询

    //模糊查询
    @GetMapping("/byNameLike")
    public Result byNameLike() {
        List<User> users  = userRepository.getByNameLike("三");
        return Result.ok().data("users",users);
    }

    @GetMapping("/nameAndAge")
    public Result ByNameAndAge() {
        List<User> users  = userRepository.getByNameAndAge("张三",20);
        return Result.ok().data("users",users);
    }
    }
