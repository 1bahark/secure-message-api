# Secure Message API

Spring Boot ile geliştirilmiş, JWT tabanlı kimlik doğrulamaya sahip kullanıcılar arası mesajlaşma REST API'si.

## Özellikler

- Kayıt ve giriş (şifreler Bcrypt ile hash'lenir)
- Spring Security ve özel JWT filtresiyle stateless kimlik doğrulama
- Giriş yapmış kullanıcılar arası mesaj gönderme, gelen ve giden kutusu
- Gönderen kimliği token'dan alınır, başkası adına mesaj atılamaz
- Merkezi hata yönetimi (400, 401, 404, 409)
- JUnit 5 ve Mockito ile birim testleri

## Teknolojiler

Java, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, JWT (jjwt), Maven, Docker, JUnit 5, Mockito

## Proje Yapısı

```
src/main/java/com/gulbahar/secure_message_api
├── config       Güvenlik ayarları (SecurityConfig)
├── controller   REST endpoint'leri
├── dto          İstek ve cevap nesneleri
├── exception    Özel hata sınıfları ve merkezi hata yöneticisi
├── model        JPA entity'leri (User, Message)
├── repository   Veritabanı erişimi
├── security     JWT filtresi
└── service      İş mantığı
```

## Kurulum

Gereksinimler: Java 17 veya üstü, Docker

**1. PostgreSQL'i başlat**

```
docker run --name msg-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=degistir -e POSTGRES_DB=messagedb -p 5432:5432 -d postgres:16
```

**2. Yerel ayar dosyasını oluştur**

```
cp src/main/resources/application-example.properties src/main/resources/application-local.properties
```

`application-local.properties` içindeki `jwt.secret` değerini en az 32 karakterlik kendi rastgele yazınla değiştir. Bu dosya `.gitignore` içinde olduğu için GitHub'a gitmez.

**3. Uygulamayı çalıştır**

```
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde açılır.

## Endpoint'ler

| Method | Adres | Açıklama | Token |
|---|---|---|---|
| POST | /auth/register | Kayıt (201, 409, 400) | Gerekmez |
| POST | /auth/login | Giriş, JWT döner (200, 401) | Gerekmez |
| POST | /messages | Mesaj gönder (201, 404, 400) | Gerekli |
| GET | /messages/inbox | Gelen mesajlar | Gerekli |
| GET | /messages/sent | Gönderilen mesajlar | Gerekli |

Korumalı endpoint'lerde başlık: `Authorization: Bearer <token>`

## Örnek İstekler

Kayıt:

```
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"username":"bahar","password":"123456"}'
```

Giriş:

```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"username":"bahar","password":"123456"}'
```

Mesaj gönderme:

```
curl -X POST http://localhost:8080/messages -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -d '{"receiverUsername":"ali","content":"Merhaba Ali!"}'
```

## Testler

```
./mvnw test
```

Servis katmanı testleri (`UserServiceTest`, `MessageServiceTest`, `JwtServiceTest`) veritabanı gerektirmez. Uygulama bağlamını açan `SecureMessageApiApplicationTests` için PostgreSQL'in çalışıyor olması gerekir.
