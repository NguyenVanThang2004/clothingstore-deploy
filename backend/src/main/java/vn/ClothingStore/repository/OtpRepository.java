package vn.ClothingStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ClothingStore.domain.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

}
