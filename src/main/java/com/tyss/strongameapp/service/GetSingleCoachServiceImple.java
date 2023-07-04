package com.tyss.strongameapp.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tyss.strongameapp.constants.NotificationConstants;
import com.tyss.strongameapp.constants.UserConstants;
import com.tyss.strongameapp.dto.CoachReviewDto;
import com.tyss.strongameapp.dto.PlanInformationDto;
import com.tyss.strongameapp.dto.SingleCoachDetailsDto;
import com.tyss.strongameapp.entity.CoachDetails;
import com.tyss.strongameapp.entity.CoachReview;
import com.tyss.strongameapp.entity.MyOrderDetails;
import com.tyss.strongameapp.entity.NotificationInformation;
import com.tyss.strongameapp.entity.PlanInformation;
import com.tyss.strongameapp.entity.PurchasedPlan;
import com.tyss.strongameapp.entity.UserInformation;
import com.tyss.strongameapp.exception.CoachNotFoundException;
import com.tyss.strongameapp.exception.PlanException;
import com.tyss.strongameapp.exception.UserNotExistException;
import com.tyss.strongameapp.exception.UserNotFoundException;
import com.tyss.strongameapp.repository.CoachDetailsRepo;
import com.tyss.strongameapp.repository.MyOrderDetailsRepository;
import com.tyss.strongameapp.repository.NotificationInformationRepository;
import com.tyss.strongameapp.repository.PlanInformationRepository;
import com.tyss.strongameapp.repository.PurchasedPlanRepo;
import com.tyss.strongameapp.repository.UserInformationRepository;
import com.tyss.strongameapp.util.MyFireBaseUtility;

/**
 * This is the implementation class to fetch single coach details and to enroll
 * coach and plan.
 * 
 * @author Sushma Guttal
 *
 */
@Service
public class GetSingleCoachServiceImple implements GetSingleCoachService {

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private CoachDetailsRepo coachRepo;

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private UserInformationRepository userRepo;

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private PlanInformationRepository planRepo;

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private PurchasedPlanRepo purchasedPlanRepo;

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private NotificationInformationRepository notificationRepo;

	/**
	 * This field is to invoke persistence layer method.
	 */
	@Autowired
	private MyOrderDetailsRepository myOrderRepository;

	@Autowired
	private MyFireBaseUtility firebaseService;

	/**
	 * This method is used to fetch coach details by id.
	 * 
	 * @param coachId, userId.
	 * @return SingleCoachDetailsDto
	 */
	@Override
	@Transactional
	public SingleCoachDetailsDto getCoachByIdAndValidateExpiry(int coachId, int userId) {
		SingleCoachDetailsDto coachDto = new SingleCoachDetailsDto();
		CoachDetails coach = coachRepo.findById(coachId)
				.orElseThrow(() -> new CoachNotFoundException("Coach Not Found"));
		UserInformation userEntity = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(UserConstants.USER_NOT_FOUND));

		BeanUtils.copyProperties(coach, coachDto);

		coachDto.setSubscribed(validateExpiry(userEntity, coach));

		List<CoachReviewDto> coachReviewDtos = new ArrayList<>();
		for (CoachReview coachReview : coach.getCoachReview()) {
			if (coachReview.getStatus().equalsIgnoreCase("APPROVED")) {
				CoachReviewDto coachReviewDto = new CoachReviewDto();
				BeanUtils.copyProperties(coachReview, coachReviewDto);
				if (coachReview.getReviewUser() != null) {
					coachReviewDto.setImageUrl(coachReview.getReviewUser().getPhoto());
					coachReviewDto.setName(coachReview.getReviewUser().getName());
					coachReviewDto.setUserId(coachReview.getReviewUser().getUserId());
				}
				coachReviewDto.setCoachId(coachReview.getReviewCoach().getCoachId());
				coachReviewDtos.add(coachReviewDto);
			}
		}
		coachDto.setCoachReview(coachReviewDtos);
		return coachDto;
	}// End of get coach by id method.

	@Transactional
	private boolean validateExpiry(UserInformation userEntity, CoachDetails coach) {
		List<PurchasedPlan> purchasedPlans = userEntity.getPurchasedPlans();
		PurchasedPlan purchasedPlan = purchasedPlans.stream()
				.filter(x -> x.getCoachDetails().getCoachId() == coach.getCoachId()).findFirst().orElse(null);
		if (purchasedPlan == null) {
			return false;
		} else if (LocalDateTime.now().isAfter(purchasedPlan.getPlanExpiryDate())) {
			purchasedPlanRepo.deleteExpiredPlan(purchasedPlan.getPurchasedPlanId());
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This method is used to enroll the plan and coach.
	 * 
	 * @param userId, coachId, planDto
	 * @return String
	 * @throws FirebaseMessagingException
	 */
	@Override
	public String enrollPlan(int userId, int coachId, PlanInformationDto planDto) throws FirebaseMessagingException {
		String response = " Coach Slots are Not Availabel.";
		PlanInformation plan = planRepo.findById(planDto.getPlanId())
				.orElseThrow(() -> new PlanException("Plan Not Found"));
		CoachDetails coach = coachRepo.findById(coachId)
				.orElseThrow(() -> new CoachNotFoundException("Coach Not Found"));
		UserInformation user = userRepo.findById(userId).orElseThrow(UserNotExistException::new);
		if (coach.getSlotsAvailable() > 0) {
			NotificationInformation notificationTwo = null;
			coach.setSlotsAvailable((coach.getSlotsAvailable()) - 1);
			user.getUserCoach().add(coach);
			notificationTwo = new NotificationInformation();
			notificationTwo.setNotificationDetails("You Have Enrolled to" + " " + coach.getCoachName() + " " + "Coach");
			notificationTwo.setNotificationType(NotificationConstants.SPECIFIC);
			user.getNotificaton().add(notificationTwo);

			user.getUserPlan().add(plan);

			NotificationInformation notification = new NotificationInformation();
			notification.setNotificationDetails("You Have Enrolled" + " " + plan.getPlanName() + " " + "Plan");
			notification.setNotificationType(NotificationConstants.SPECIFIC);
			user.getNotificaton().add(notification);

			Date date = new Date();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, 330);

			SimpleDateFormat sdfNow = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
			String s1 = sdfNow.format(calendar.getTime());

			NotificationInformation notificationThree = new NotificationInformation();
			notificationThree.setNotificationDetails(user.getName() + "(" + user.getEmail() + ")" + " "
					+ "Has Enrolled to Coach," + " " + coach.getCoachName() + "(" + coach.getEmailId() + ")" + " "
					+ "and" + " " + plan.getPlanName() + " " + "Plan of Cost" + " " + plan.getPlanPrice() + " " + "On"
					+ " " + s1);
			notificationThree.setNotificationType("plan");
			notificationRepo.save(notificationThree);

			MyOrderDetails myOrder = new MyOrderDetails();
			myOrder.setName(plan.getPlanName());
			myOrder.setPrice(plan.getPlanPrice());
			myOrder.setType("plan");
			myOrder.setUserMyOrder(user);
			myOrderRepository.save(myOrder);
			user.getMyorder().add(myOrder);

			userRepo.save(user);
			savePurchasedPlanDetails(user, plan, coach);
			firebaseService.sendTokenNotification(user.getFirebaseToken(), notificationTwo.getNotificationDetails(),
					notificationTwo.getNotificationImage());

			firebaseService.sendTokenNotification(user.getFirebaseToken(), notification.getNotificationDetails(),
					notification.getNotificationImage());

			response = " Plan Enrolled Successfully";
		}
		return response;

	}// End of enroll plan method.

	@Transactional
	private void savePurchasedPlanDetails(UserInformation user, PlanInformation plan, CoachDetails coach) {
		long noOfDays = (long) (plan.getNoOfWeeks() * 30);
		LocalDateTime expiryDate = LocalDateTime.now().plusDays(noOfDays);
		PurchasedPlan purchasedPlan = PurchasedPlan.builder().userPurchasedPlan(user).coachDetails(coach)
				.planPurchased(plan).planExpiryDate(expiryDate).build();
		purchasedPlanRepo.save(purchasedPlan);
	}

	/**
	 * This method is implemented to fetch plan details
	 * 
	 * @param planId
	 * @return PlanInformationDto
	 */
	@Override
	public PlanInformationDto getPlanById(int planId) {
		Optional<PlanInformation> planOptionalEntity = planRepo.findById(planId);
		if (planOptionalEntity.isPresent()) {
			PlanInformationDto planDto = new PlanInformationDto();
			PlanInformation planEntity = planOptionalEntity.get();
			BeanUtils.copyProperties(planEntity, planDto);
			double savings = ((planEntity.getPlanDiscount() * planEntity.getPlanPrice()) / 100);
			planDto.setFinalPrice(planEntity.getPlanPrice() - savings);
			return planDto;
		}
		return null;
	}// End of getPlanById method.

}// End of GetSingleCoachServiceImple class.
