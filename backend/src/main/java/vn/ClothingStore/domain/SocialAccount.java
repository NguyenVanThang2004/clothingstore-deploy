package vn.ClothingStore.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "socialAccounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String provider ; // ten nha social network
    private String providerId ;
    private String email ; // email tai khoan
    private String name  ; // ten nguoi dung

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user ;
}
