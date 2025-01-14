package com.project.repositories;

import com.project.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepoImpl {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepoImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserEntity> getUserForSA(){
        Query query = new Query();
        query.addCriteria((Criteria.where("email").regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
//                       or
//        Criteria criteria = new Criteria();
//        query.addCriteria(criteria.andOperator((Criteria.where("email").regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$")), Criteria.where("sentimentAnalysis").is(true)) );
        List<UserEntity> users = mongoTemplate.find(query, UserEntity.class);
        return users;
    }
}
