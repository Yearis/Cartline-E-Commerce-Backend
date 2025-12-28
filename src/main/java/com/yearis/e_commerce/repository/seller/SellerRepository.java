package com.yearis.e_commerce.repository.seller;

import com.yearis.e_commerce.entity.Address;
import com.yearis.e_commerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    boolean existsByStoreName(String storeName);

    boolean existsByBusinessPhoneNumber(String businessPhoneNumber);

    boolean existsByAddress(Address address);

    Page<Seller> findSellersByStoreNameContaining(String name, Pageable pageable);
}
