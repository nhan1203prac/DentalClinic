# Tác nhân, use case và công nghệ

| | |
|---|---|
| Sinh viên | Võ Thành Nhân – MSSV 22115053122234 – Lớp 22T2 |
| Giảng viên hướng dẫn | Hoàng Thị Mỹ Lệ |
| Tên đề tài | Xây dựng website quản lý phòng khám nha khoa, tích hợp trợ lý hỗ trợ tra cứu tài liệu nội bộ |
| Công nghệ | Spring Boot (Java 21) · MySQL 8 · React + Vite (JavaScript) · Gemini API · Qdrant |
| Thiết kế CSDL | 18 bảng, 25 quan hệ (bản ERD đã duyệt) |

> Quy tắc nghiệp vụ (mã BR-xx) và API: xem [requirements.md](requirements.md). Sơ đồ CSDL: [erd.png](erd.png).

## 1. Tác nhân và use case

**Quy ước:** use case có gạch chân trong file Word là use case **ghi dữ liệu**. Cột "Bảng" bên dưới là các bảng bị ghi.

### 1.1. Khách vãng lai
| Use case | Bảng |
|---|---|
| Đăng ký tài khoản (chỉ tạo tài khoản) | TAI_KHOAN |
| Xem thông tin phòng khám | – |
| Xem thông tin bác sĩ | – |
| Xem danh mục dịch vụ | – |

### 1.2. Bệnh nhân
| Use case | Bảng |
|---|---|
| Đăng nhập | – |
| Xem / Cập nhật thông tin cá nhân | TAI_KHOAN |
| **Đặt lịch khám** | BENH_NHAN, TIEN_SU_BENH, LICH_HEN, CHI_TIET_LICH_HEN |
| Đặt lịch tái khám | LICH_HEN, CHI_TIET_LICH_HEN |
| Xem lịch khám | – |
| Hủy lịch khám | LICH_HEN |
| Xem hồ sơ khám bệnh (kết quả khám, sơ đồ răng, lộ trình) | – |
| Xem thông báo hủy lịch, nhắc tái khám | (đánh dấu đã đọc) |

### 1.3. Nhân viên phòng khám
| Use case | Bảng |
|---|---|
| Đăng nhập | – |
| **Tạo hồ sơ khám** (tiếp nhận, cấp số thứ tự) | HO_SO_KHAM, LICH_HEN |
| Xem thông tin bệnh nhân, Xem lịch sử khám của bệnh nhân | – |
| Hủy lịch khám (khi bác sĩ nghỉ) | LICH_HEN, THONG_BAO |
| Thêm mới danh mục dịch vụ | DICH_VU |
| Tương tác với trợ lý ảo | HOI_THOAI, TIN_NHAN |

### 1.4. Bác sĩ
| Use case | Bảng |
|---|---|
| Đăng nhập, Xem lịch khám, Xem hồ sơ khám của bệnh nhân | – |
| **Cập nhật hồ sơ khám bệnh** | HO_SO_KHAM, SO_DO_RANG, CHI_TIET_LICH_HEN, TIEN_SU_BENH, LO_TRINH_DIEU_TRI, BUOC_DIEU_TRI, LICH_HEN |
| Xem lịch làm việc | – |
| Tương tác với trợ lý ảo | HOI_THOAI, TIN_NHAN |

### 1.5. Chủ phòng khám (Admin)
| Use case | Bảng |
|---|---|
| Đăng nhập | – |
| Quản lý tài khoản nhân viên, bác sĩ | TAI_KHOAN, NHAN_VIEN, BAC_SI |
| Cập nhật danh mục dịch vụ | DICH_VU |
| **Sắp lịch làm việc theo tuần** | LICH_LAM_VIEC |
| Tương tác với trợ lý ảo | HOI_THOAI, TIN_NHAN |
| (Quản lý tài liệu nội bộ: tải lên, xóa) | TAI_LIEU |

### 1.6. Trợ lý ảo
| Use case | Bảng |
|---|---|
| Tra cứu tài liệu nội bộ | TAI_LIEU, TIN_NHAN, TRICH_DAN |
| Tóm tắt hồ sơ khám | TIN_NHAN |
| Phân tích triệu chứng | TIN_NHAN |

---

## 2. Phân quyền

| Chức năng | Khách | Bệnh nhân | Nhân viên | Bác sĩ | Chủ PK |
|---|:-:|:-:|:-:|:-:|:-:|
| Xem phòng khám, bác sĩ, dịch vụ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Đặt, hủy lịch của mình; xem hồ sơ của mình | | ✓ | | | |
| Tạo hồ sơ khám, hủy lịch kèm thông báo | | | ✓ | | |
| Thêm dịch vụ | | | ✓ | | ✓ |
| Sửa dịch vụ | | | | | ✓ |
| Xem bệnh nhân, lịch sử khám | | | ✓ | ✓ | ✓ |
| Cập nhật hồ sơ khám, lộ trình | | | | ✓ | |
| Xem lịch làm việc | | | ✓ | ✓ (của mình) | ✓ |
| Sắp lịch làm việc tuần | | | | | ✓ |
| Quản lý tài khoản nhân viên, bác sĩ | | | | | ✓ |
| Tải lên, xóa tài liệu | | | ✓ | | ✓ |
| Trợ lý ảo | | | ✓ | ✓ | ✓ |

Spring Security đọc `vai_tro` thành `ROLE_BENH_NHAN`, `ROLE_NHAN_VIEN`, `ROLE_BAC_SI`, `ROLE_CHU_PHONG_KHAM`. Mọi API của bệnh nhân **kiểm tra quyền sở hữu**: hồ sơ hoặc lịch phải thuộc `ma_tai_khoan` đang đăng nhập.

---

## 3. Kiến trúc và công nghệ

```
┌──────────────────────────── React + Vite (JavaScript) ────────────────────────────┐
│ Trang công khai · Bệnh nhân · Nhân viên · Bác sĩ · Chủ phòng khám · Khung chat AI │
└───────────────────────────────────────┬───────────────────────────────────────────┘
                                        │ REST JSON + JWT
┌───────────────────────────────────────▼───────────────────────────────────────────┐
│ Spring Boot 3 (Java 21) – monolith chia module                                    │
│ security · tai-khoan · danh-muc · lich-lam-viec · dat-lich · kham · lo-trinh ·    │
│ thong-bao · tai-lieu (ingestion) · tro-ly (RAG) · scheduler                       │
└───────┬─────────────────────────────┬───────────────────────────────┬─────────────┘
        │ JPA                         │ REST                          │ HTTPS
┌───────▼────────┐          ┌─────────▼─────────┐            ┌────────▼────────┐
│ MySQL 8        │          │ Qdrant            │            │ Gemini API      │
│ 18 bảng (ERD)  │          │ vector các đoạn   │            │ embedding + LLM │
└────────────────┘          │ tài liệu          │            └─────────────────┘
                            └───────────────────┘
File tài liệu gốc lưu ở thư mục ./storage/tai-lieu (TAI_LIEU.duong_dan_file)
```

| Thành phần | Lựa chọn |
|---|---|
| Backend | Java 21, Spring Boot 3.x: Web, Data JPA, Security, Validation; Lombok, MapStruct, springdoc-openapi (không dùng Flyway, CSDL tạo bằng script) |
| CSDL | MySQL 8.0 (InnoDB, utf8mb4), driver `mysql-connector-j`; tạo bằng script `database/PhongKhamNhaKhoa_mysql.sql`, JPA chỉ `validate` |
| Xác thực | JWT (access token 30 phút, refresh token 7 ngày), BCrypt |
| Đọc tài liệu | Apache PDFBox (PDF), Apache POI (DOCX) |
| Vector DB | Qdrant chạy bằng file thực thi tải từ GitHub (không cần Docker), cổng 6333; collection `tai_lieu_chunks`, khoảng cách cosine |
| AI | Gemini API: model chat dòng Flash, model embedding của Gemini. **Kiểm tra tên model mới nhất trên Google AI Studio khi cài đặt.** Gọi bằng `RestClient` |
| Frontend | React 18, Vite (JavaScript, JSX), React Router, TanStack Query, Axios, Ant Design (locale vi_VN), Day.js |
| Sơ đồ răng | Component SVG tự dựng (32 răng theo FDI) |
| Chạy demo | Cài trực tiếp trên máy: MySQL 8, Qdrant, backend (`mvn spring-boot:run` hoặc file .jar), frontend (`npm run dev` hoặc bản build). Docker để sau, không bắt buộc |

---
