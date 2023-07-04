package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList = webSeriesRepository.findAll(); // getting list of all webseries
        User user = userRepository.findById(userId).get(); // getting user by userId
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType(); // extracting user subscription type

        int userAge = user.getAge();

        int count = 0;

        for(WebSeries webSeries : webSeriesList){
            if(webSeries.getAgeLimit() <= userAge){
                if(userSubscriptionType == SubscriptionType.ELITE) count++;
                else if(userSubscriptionType == SubscriptionType.PRO
                    && (webSeries.getSubscriptionType() == SubscriptionType.PRO
                    || webSeries.getSubscriptionType() == SubscriptionType.BASIC)) count++;
                else if(userSubscriptionType == SubscriptionType.BASIC
                    && webSeries.getSubscriptionType() == SubscriptionType.BASIC) count++;
            }
        }
        return count;

    }


}
