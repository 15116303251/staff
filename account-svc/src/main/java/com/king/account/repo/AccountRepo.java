package com.king.account.repo;

import com.king.account.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {

    Account findAccountByEmail(String email);

    Account findAccountByPhoneNumber(String phoneNumber);

    Page<Account> findAll(Pageable pageable);
    
    long countByConfirmedAndActive(boolean confirmedAndActive);
    
    long countByEmailIsNotNull();
    
    long countByPhoneNumberIsNotNull();
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.memberSince >= ?1")
    long countByMemberSinceAfter(Instant date);
}
