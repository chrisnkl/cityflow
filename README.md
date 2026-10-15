# Cityflow - Backend
This project is the backend system for Cityflow project - a MSc assignment that involves a mobile native android application and this system.

# How to run
#### 1. Ensure you have docker and azurite installed in your system.
#### 2. Go into the cf-backend folder or click this 
```bash
cd cf-backend
```
#### 3. Type the command `docker compose up -d` or click this 
```bash
docker compose up -d
```
#### 4. Go back into the root folder or click this
```bash
cd ../
```
#### 5. Run azurite by typing azurite in the terminal or click this 
```bash 
azurite
```
#### 6. Finally, run the application with the **dev** profile or click these last steps
```bash
cd cf-backend
```

### 7. Run the spring boot application with dev as the profile
```bash
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Note: If cd commands do not work, they depend on where you currently are. Therefore, It is easier and safer to type the cd on the terminal.
## General Instructions:
1. Run the command `azurite` in the root folder.
2. Run the command `docker compose up -d` on the cf-backend (spring-boot folder)
3. Run the spring boot application with active profile = dev