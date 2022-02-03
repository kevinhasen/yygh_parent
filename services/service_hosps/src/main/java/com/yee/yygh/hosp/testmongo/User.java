package com.yee.yygh.hosp.testmongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * ClassName: User
 * Description:
 * date: 2021/12/27 19:57
 * MongoDB实体,id必须是字符串
 * @author Yee
 * @since JDK 1.8
 */
@Data
@Document("User")  //文档型数据库
public class User {
    //MongoDB的id必须是字符串
    @Id
    private String id;
    private String name;
    private Integer age;
    private String email;
    private String createDate;

}
