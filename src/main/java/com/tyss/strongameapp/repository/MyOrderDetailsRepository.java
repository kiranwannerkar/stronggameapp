package com.tyss.strongameapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tyss.strongameapp.entity.MyOrderDetails;

public interface MyOrderDetailsRepository extends JpaRepository<MyOrderDetails, Integer> {

}
