# Website quản lý phòng khám nha khoa tích hợp trợ lý tra cứu tài liệu nội bộ

Đồ án tốt nghiệp – Võ Thành Nhân (22115053122234, lớp 22T2) – GVHD: Hoàng Thị Mỹ Lệ.

Website cho phòng khám nha khoa nhỏ: chủ phòng khám sắp lịch làm việc của bác sĩ theo tuần; bệnh nhân đặt lịch online theo khung 30 phút; nhân viên tiếp nhận và tạo hồ sơ khám; bác sĩ ghi kết quả khám, sơ đồ răng, dịch vụ đã làm và lập lộ trình điều trị; hệ thống tự nhắc tái khám. Trợ lý AI giúp nhân viên, bác sĩ tra cứu tài liệu nội bộ (quy trình, phác đồ, bảng giá, bảo hành) có trích dẫn nguồn, tóm tắt hồ sơ và gợi ý phân tích triệu chứng.

Không làm: kho vật tư, thanh toán, doanh thu, đặt lịch qua điện thoại.

## Tài liệu

| File | Nội dung |
|---|---|
| [docs/requirements.md](docs/requirements.md) | Phạm vi, quy tắc nghiệp vụ (mã BR-xx), yêu cầu phi chức năng, trợ lý AI, API, màn hình, kịch bản demo |
| [docs/usecase.md](docs/usecase.md) | Tác nhân, use case, phân quyền, kiến trúc |
| [docs/erd.png](docs/erd.png) | Sơ đồ ERD – 18 bảng, 25 quan hệ |
| [docs/THIET_KE_CSDL.md](docs/THIET_KE_CSDL.md) | Chi tiết từng bảng, cột, ràng buộc; cấu hình kết nối và lưu ý khi viết JPA |
| [database/PhongKhamNhaKhoa_mysql.sql](database/PhongKhamNhaKhoa_mysql.sql) | Script tạo CSDL MySQL (nguồn chuẩn) |
| [database/PhongKhamNhaKhoa_seed_mysql.sql](database/PhongKhamNhaKhoa_seed_mysql.sql) | Dữ liệu mẫu, mật khẩu mọi tài khoản `123456` |

CSDL tạo bằng 2 script trên (chạy bằng MySQL Workbench hoặc lệnh `mysql`). Không dùng Flyway, không để JPA tự tạo bảng (`ddl-auto: validate`).

## Công nghệ

| Phần | Công nghệ |
|---|---|
| Backend | Java 20, Gradle, Spring Boot 3 (Web, Data JPA, Security, Validation), JWT, Lombok, springdoc-openapi |
| CSDL | MySQL 8.0 (InnoDB, utf8mb4) |
| Frontend | React 18 + Vite (JavaScript, JSX), Ant Design, React Router, TanStack Query, Axios |
| Trợ lý AI | Gemini API (embedding và sinh câu trả lời), Qdrant (lưu vector), PDFBox và POI (đọc PDF, DOCX) |

## Quy tắc code

- Tên bảng, cột giữ đúng như CSDL (`LICH_HEN`, `gio_hen`). Class Java PascalCase không dấu (`LichHen`), JSON camelCase (`maLichHen`).
- Mỗi module chia `entity`, `repository`, `service`, `controller`, `dto`. Controller không gọi thẳng repository. API trả DTO, không trả entity.
- Logic nghiệp vụ đặt ở service. Thao tác ghi nhiều bảng dùng `@Transactional`.
- Ràng buộc nào CSDL làm được thì để CSDL làm; code bắt lỗi và trả mã HTTP phù hợp (400, 403, 404, 409).
- Comment:
  - Viết tiếng Việt, ngắn, một dòng là chính.
  - Chỉ comment chỗ cần giải thích lý do hoặc quy tắc nghiệp vụ, kèm mã quy tắc, ví dụ `// BR-21: mỗi khung chỉ một lịch còn hiệu lực`.
  - Không comment điều đọc code là thấy, không viết đoạn văn dài, không viết kiểu liệt kê sáo rỗng.
- Không commit mật khẩu, API key: đặt trong `application-local.properties` và `.env.local`.
- Commit nhỏ, tiêu đề rõ: `feat(dat-lich): api khung trong`, `fix(kham): sai so thu tu`.

## Các bước cần làm

Mức ưu tiên: **[P1]** bắt buộc cho demo, **[P2]** nên có, **[P3]** nếu còn thời gian.

### Giai đoạn 0 – Chuẩn bị môi trường
- [ ] [P1] Cài JDK 21, Maven, Node 20, MySQL 8.0 + Workbench, IntelliJ hoặc VS Code
- [ ] [P1] Tạo repo Git `DATN`, `.gitignore` (bỏ `application-local.properties`, `.env.local`, `storage/`, `node_modules/`, `target/`), nhánh `main` và `dev`
- [x] [P1] Script `PhongKhamNhaKhoa_mysql.sql` và dữ liệu mẫu – đã chạy thử trên MySQL 8.0
- [ ] [P1] Chạy 2 script trên máy mình (tạo bảng, rồi dữ liệu mẫu)
- [ ] [P1] Tạo API key Gemini (Google AI Studio), chỉ lưu trong `application-local.properties`

### Giai đoạn 1 – Nền tảng backend
- [x] [P1] Khởi tạo Spring Boot (Web, Data JPA, Security, Validation, Lombok, mysql-connector-j, springdoc-openapi)
- [x] [P1] Cấu hình datasource MySQL, `ddl-auto=validate`
- [ ] [P1] Viết 18 entity, enum trạng thái, repository (theo `docs/THIET_KE_CSDL.md`) – đã viết và compile, chưa chạy `validate` với MySQL thật
- [x] [P1] GlobalExceptionHandler và định dạng lỗi chung
- [ ] [P1] JWT: đăng ký (BR-01), đăng nhập, refresh, `/auth/me`, phân quyền theo vai trò – đã viết và compile, chưa thử với DB thật
- [x] [P2] Swagger UI để thử API

### Giai đoạn 2 – Nền tảng frontend
- [ ] [P1] Vite, React, JS (JSX), Ant Design (vi_VN), React Router, TanStack Query, Axios (`withCredentials`, interceptor gắn access token lưu trong bộ nhớ, gặp 401 thì gọi `/auth/refresh` bằng cookie rồi gửi lại) – đã viết, `npm run build` qua, chưa thử với backend thật
- [ ] [P1] AuthContext, trang Đăng nhập và Đăng ký, ProtectedRoute theo vai trò – đã viết, chưa thử với backend thật
- [ ] [P1] Layout công khai và layout ứng dụng (menu theo vai trò) – đã viết, màn hình các giai đoạn sau là trang giữ chỗ
- [ ] [P1] Trang công khai: bác sĩ, dịch vụ – đã viết kèm API `/public/bac-si`, `/public/dich-vu`, chưa thử với backend thật

### Giai đoạn 3 – Quản trị danh mục
- [ ] [P1] API và màn hình quản lý nhân viên, bác sĩ (tạo tài khoản kèm hồ sơ, khóa tài khoản) – đã viết API và compile, chưa làm màn hình
- [ ] [P1] API và màn hình dịch vụ (nhân viên thêm, chủ phòng khám sửa và ngưng) – đã viết API và compile, chưa làm màn hình
- [ ] [P2] Tải ảnh đại diện bác sĩ

### Giai đoạn 4 – Lịch làm việc
- [ ] [P1] API lấy lịch theo tuần, lưu lưới tuần (thêm, sửa, xóa ô chưa có lịch hẹn)
- [ ] [P1] Sao chép tuần trước (BR-13)
- [ ] [P1] Chuyển nghỉ hoặc rút giờ, trả danh sách lịch hẹn bị ảnh hưởng (BR-15, BR-16)
- [ ] [P1] Màn hình lưới tuần cho chủ phòng khám, màn hình xem lịch cho bác sĩ
- [ ] [P3] Job tự sao chép lịch tuần (BR-14)

### Giai đoạn 5 – Đặt lịch ⭐ (luồng chính)
- [ ] [P1] `KhungTrongService`: cắt khung 30 phút, bỏ giờ nghỉ trưa, bỏ khung đã đặt, bỏ giờ đã qua (BR-20)
- [ ] [P1] API khung trống theo ngày, trả về tất cả bác sĩ kèm khung
- [ ] [P1] API đặt lịch trong một transaction (BR-21 đến BR-25), bắt lỗi vi phạm `UQ_LICH_HEN_lich_gio` trả về 409
- [ ] [P1] Màn hình đặt lịch: chọn hoặc thêm người khám, tiền sử, lý do, dịch vụ tùy chọn, ngày, thẻ bác sĩ kèm nút giờ, xác nhận
- [ ] [P1] Lịch khám của tôi và hủy lịch (BR-26)
- [ ] [P1] Unit test: cắt khung, trùng khung, trùng giờ bệnh nhân, đặt ngoài giờ làm

### Giai đoạn 6 – Tiếp nhận và khám
- [ ] [P1] Màn hình tiếp nhận hôm nay, tạo hồ sơ khám, cấp số thứ tự (BR-30)
- [ ] [P1] Hàng chờ của bác sĩ, chuyển trạng thái đang khám và hoàn thành (BR-31)
- [ ] [P1] Màn hình khám: thông tin và cảnh báo tiền sử, form khám, chốt và thêm dịch vụ (BR-33, BR-34)
- [ ] [P1] Component **Odontogram SVG** (32 răng FDI, chọn tình trạng theo màu), lưu SO_DO_RANG
- [ ] [P1] Nhân viên hủy lịch do bác sĩ nghỉ, sinh thông báo
- [ ] [P2] Job chuyển vắng mặt cuối ngày (BR-32)

### Giai đoạn 7 – Lộ trình và tái khám
- [ ] [P1] Lập lộ trình và các bước trong màn hình khám, đánh dấu bước làm ngay qua dòng chi tiết (BR-40, BR-41)
- [ ] [P1] Kiểm tra bước thuộc đúng bệnh nhân (BR-42)
- [ ] [P1] Job nhắc tái khám (BR-43)
- [ ] [P1] Đặt lịch tái khám từ bước hoặc từ thông báo (BR-44), tự hoàn thành lộ trình (BR-45)

### Giai đoạn 8 – Hồ sơ và thông báo
- [ ] [P1] API hồ sơ khám bệnh tổng hợp, màn hình cho bệnh nhân và bác sĩ (dòng thời gian, sơ đồ răng theo lần khám, lộ trình)
- [ ] [P1] Thông báo: danh sách, đã đọc, biểu tượng chuông có số chưa đọc
- [ ] [P2] Tra cứu bệnh nhân cho nhân viên và bác sĩ (theo tên, số điện thoại của tài khoản)

### Giai đoạn 9 – Trợ lý ảo ⭐
- [ ] [P1] Tải và chạy Qdrant (file thực thi, cổng 6333)
- [ ] [P1] Biên soạn 20–40 tài liệu mẫu: quy trình, phác đồ, bảng giá, chính sách bảo hành
- [ ] [P1] `GeminiClient` (embedding và chat), `QdrantClient` (tạo collection, upsert, search có filter, xóa theo `ma_tai_lieu`)
- [ ] [P1] Ingestion: tải lên, trích văn bản, chia đoạn, embedding, lưu Qdrant; màn hình quản lý tài liệu
- [ ] [P1] Tra cứu có trích dẫn (BR-61, BR-62), lưu TIN_NHAN và TRICH_DAN
- [ ] [P1] Khung chat: danh sách hội thoại, tin nhắn, thẻ nguồn mở được file
- [ ] [P2] Tóm tắt hồ sơ khám (nút trong màn hình khám)
- [ ] [P2] Phân tích triệu chứng
- [ ] [P2] Bộ đánh giá 30–50 câu, đo recall@5 và độ đúng (docs/requirements.md mục 4.5)

### Giai đoạn 10 – Hoàn thiện
- [ ] [P1] Dữ liệu mẫu đầy đủ để demo trọn luồng: đặt lịch, khám, lộ trình, tái khám, trợ lý
- [ ] [P1] Kiểm thử thủ công theo kịch bản demo; sửa lỗi giao diện responsive
- [ ] [P2] Viết hướng dẫn cài đặt chi tiết, chụp màn hình, viết chương cài đặt và đánh giá trong báo cáo
- [ ] [P3] Đóng gói Docker nếu cần demo trên máy khác
- [ ] [P3] Nhật ký thao tác (audit log), đổi mật khẩu, quên mật khẩu
