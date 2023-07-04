package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);


        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int subscriptionAmount = 0;
        int noOfScreenSubscribed = subscriptionEntryDto.getNoOfScreensRequired();

        if(subscriptionType == SubscriptionType.BASIC) {
            subscriptionAmount = 500 + (noOfScreenSubscribed * 200);
        }
        else if(subscriptionType == SubscriptionType.PRO){
            subscriptionAmount = 800 + (noOfScreenSubscribed * 250 );
        }
        else {
            subscriptionAmount = 1000 + (noOfScreenSubscribed * 350);
        }
        subscription.setTotalAmountPaid(subscriptionAmount);

        // set subscription to user
        user.setSubscription(subscription);
        // set user to  subscription
        subscription.setUser(user);
        // save the parent
        userRepository.save(user);

        return subscriptionAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription currentSubscription = user.getSubscription();
        if(currentSubscription.getSubscriptionType() == SubscriptionType.ELITE)
        {
            throw new Exception("Already the best Subscription");
        }

        int amountToBePaid = 0;
        // if current subscription is pro (pro -> elite)
        if(currentSubscription.getSubscriptionType() == SubscriptionType.PRO)
        {
            int currentAmount = currentSubscription.getTotalAmountPaid();
            int amountNeedToUpgrade = 1000 + (350 * currentSubscription.getNoOfScreensSubscribed());

            currentSubscription.setSubscriptionType(SubscriptionType.ELITE);
            currentSubscription.setTotalAmountPaid(amountNeedToUpgrade);
            amountToBePaid = amountNeedToUpgrade - currentAmount;
            user.setSubscription(currentSubscription);
            userRepository.save(user);
        }
        else // if current subscription is basic (basic -> pro)
        {
            int currentAmount = currentSubscription.getTotalAmountPaid();
            int amountNeedToUpgrade = 800 + (250 * currentSubscription.getNoOfScreensSubscribed());

            currentSubscription.setSubscriptionType(SubscriptionType.PRO);
            currentSubscription.setTotalAmountPaid(amountNeedToUpgrade);
            amountToBePaid = amountNeedToUpgrade - currentAmount;
            user.setSubscription(currentSubscription);
            userRepository.save(user);
        }
        return amountToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalRevenue = 0;
        for(Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
