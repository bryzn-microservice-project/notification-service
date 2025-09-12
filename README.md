################################################################
#                                                              #
#                   NOTIFICATION-SERVICE                       #
#                                                              #
################################################################

{
  "topicName": "LoginResponse",
  "correlatorId": 12345,
  "status": "SUCCESSFUL",
  "username": "bnguyen",
  "timestamp": "2025-09-12T22:15:30Z"
}

{
  "topicName": "RewardsResponse",
  "correlatorId": 98765,
  "email": "john.doe@example.com",
  "username": "johndoe",
  "rewardPoints": 1200,
  "application": "SUCCESS",
  "timestamp": "2025-09-12T22:30:45Z"
}

{
  "topicName": "NewAccountResponse",
  "correlatorId": 45678,
  "username": "new_user123",
  "status": "SUCCESSFUL",
  "statusMessage": "Account created successfully"
}

{
  "topicName": "PaymentResponse",
  "correlatorId": 20001,
  "paymentAmount": 149.99,
  "email": "customer.one@example.com",
  "creditCard": "4111111111111111",
  "timestamp": "2025-09-12T23:00:00Z",
  "status": "SUCCESSFUL"
}