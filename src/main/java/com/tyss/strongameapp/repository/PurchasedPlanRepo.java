package com.tyss.strongameapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tyss.strongameapp.entity.PurchasedPlan;

public interface PurchasedPlanRepo extends JpaRepository<PurchasedPlan, Integer> {

	@Modifying
	@Query(value = " delete from PurchasedPlan where purchasedPlanId=?1")
	void deleteExpiredPlan(int purchasedPlanId);
}
