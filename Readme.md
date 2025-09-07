# Bouncer API 

Bouncer API is a modern **Authentication-as-a-Service (AaaS)** solution that allows developers to easily integrate user authentication (registration, login, logout, and session validation) into their applications. Built with **Spring Boot** and designed for simplicity, security, and scalability.

This is the backend API for
[Bouncer Console](https://github.com/sudoMakeMeCoffee/bouncer-api)


## 🚀 Features

- User **Registration** with `x-api-key` authentication
- **Login** with JWT tokens stored in HTTP-only cookies
- **Logout** to clear user sessions
- **Check Authentication** endpoint to validate access tokens
- Minimalistic, **modern API documentation** with syntax-highlighted examples
- Fully responsive **HTML/CSS front-end** for documentation

---

## 📦 Installation

1. Clone the repository:

```bash
git clone https://github.com/sudoMakeMeCoffee/bouncer-api.git
cd bouncer-api
```

2. Build the project using Maven:

```bash
./mvnw clean install
```

3. Configure environment variables and application.properties:

```env

# ==============================
# =  PostgreSQL Configuration =
# ==============================
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# ===========================
# = JPA / Hibernate Options =
# ===========================

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect


# ===========================
# = SMTP Gmail Configuration =
# ===========================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${GMAIL_USERNAME}
spring.mail.password=${GMAIL_APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true


```

4. Run the application:

```bash
./mvnw spring-boot:run
```

---

## 📚 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/users/register` | POST | Register a new user |
| `/api/v1/users/login` | POST | Authenticate a user |
| `/api/v1/users/logout` | POST | Logout user and clear cookies |
| `/api/v1/users/authenticated` | POST | Check if user is authenticated |

### Example Usage (JavaScript Fetch)

```js
// Registration
fetch("https://bouncer-api-production.up.railway.app/api/v1/users/register", {
  method: "POST",
  headers: { "Content-Type": "application/json", "x-api-key": "YOUR_API_KEY" },
  body: JSON.stringify({ email: "test@example.com", password: "12345678" })
});
```

---

## 🌐 Documentation

The API documentation is available directly in this repository under `index.html`. It features:

- Syntax-highlighted code blocks
- Section navigation for **Register**, **Login**, **Logout**, **Check Auth**
- Responsive layout for desktop and mobile
- Modern UI with subtle hover effects

Open the file in a browser:

```bash
open index.html
```

Or navigate to [Bouncer API Documentation](https://bouncer-api-production.up.railway.app/)


---

## 🔒 Security

- All sensitive endpoints require an `x-api-key`
- JWT stored in **HTTP-only cookies** to prevent XSS
- Secure cookie handling and token validation

---


## ⚡ Links

- [Live Documentation](https://bouncer-api-production.up.railway.app/)
- [Bouncer Console Repository](https://github.com/sudoMakeMeCoffee/bouncer-console)

