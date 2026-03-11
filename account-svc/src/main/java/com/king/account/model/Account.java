package com.king.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;

    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "confirmed_and_active")
    private boolean confirmedAndActive;

    @Column(name = "member_since")
    private Instant memberSince;

    @Column(name = "support")
    private boolean support;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "password_hash")
    private String passwordHash;
}
