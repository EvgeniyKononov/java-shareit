# Java-shareit
Backend of application for booking items.

# Description
REST based on Spring Boot, Maven, Lombock, PostgreSQL, Hibernate, Docker

ER-diagram
![ER-diagram](ER%20diagram.JPG)

Application allow users to create items which they ready to borrow to another user.
Another user can book these items and leave comments after using. 
Also users can to create request for items which they want to book but can't find it in base by searching.

Application developed with multimodal architecture:
1) Gateway module - using for validate all requests to API and when send it to server part and revert answer form server
2) Server module - using for handle request from Gateway service, handle it, take date from repository and revert answer to Gateway.

# Deploying

For deploying project you must have Docker. Use command _docker compose up_ from root directory to deploy images and containers.
After that will be possible to send requests to Gateway container. Project has directory _postman_ where tests for Postman can be taken.