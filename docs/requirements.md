# Đặc tả yêu cầu và nghiệp vụ

| | |
|---|---|
| Sinh viên | Võ Thành Nhân – MSSV 22115053122234 – Lớp 22T2 |
| Giảng viên hướng dẫn | Hoàng Thị Mỹ Lệ |
| Tên đề tài | Xây dựng website quản lý phòng khám nha khoa, tích hợp trợ lý hỗ trợ tra cứu tài liệu nội bộ |
| Công nghệ | Spring Boot (Java 21) · MySQL 8 · React + Vite (JavaScript) · Gemini API · Qdrant |
| Thiết kế CSDL | 18 bảng, 25 quan hệ (bản ERD đã duyệt) |

> Tác nhân, use case, phân quyền và công nghệ: xem [usecase.md](usecase.md). Thiết kế CSDL: [erd.png](erd.png), [THIET_KE_CSDL.md](THIET_KE_CSDL.md), script `../database/PhongKhamNhaKhoa_mysql.sql`.

## 1. Tổng quan

Hệ thống số hóa luồng nghiệp vụ chính của một phòng khám nha khoa quy mô nhỏ:

1. **Lịch làm việc của bác sĩ**: chủ phòng khám sắp lịch theo tuần.
2. **Đặt lịch khám online**: bệnh nhân tự đặt, chọn bác sĩ và khung giờ 30 phút còn trống. Không đặt qua điện thoại, không đặt tại quầy.
3. **Tiếp nhận và khám**: nhân viên tạo hồ sơ khám và cấp số thứ tự. Bác sĩ ghi kết quả khám, sơ đồ răng, các dịch vụ đã làm.
4. **Lộ trình điều trị và tái khám**: bác sĩ lập lộ trình nhiều bước, hệ thống tự nhắc tái khám, bệnh nhân đặt lịch theo từng bước.
5. **Thông báo**: báo hủy lịch, nhắc tái khám.
6. **Trợ lý ảo (AI)** cho nhân viên, bác sĩ và chủ phòng khám:
   - **Tra cứu tài liệu nội bộ** (quy trình, phác đồ, bảng giá, chính sách bảo hành), có **trích dẫn nguồn**. Dùng kỹ thuật RAG.
   - **Tóm tắt hồ sơ khám** của một bệnh nhân.
   - **Phân tích triệu chứng** để gợi ý cho bác sĩ tham khảo.

### 1.1. Ngoài phạm vi
- Quản lý kho vật tư, thanh toán, hóa đơn, doanh thu.
- Đặt lịch qua điện thoại hoặc tại quầy, khách vãng lai đến khám không đặt lịch.
- Quản lý hồ sơ người thân như một chức năng riêng. Hồ sơ chỉ sinh ra khi đặt lịch.
- Xin nghỉ và duyệt nghỉ của bác sĩ. Chủ phòng khám chỉnh trực tiếp trên lịch tuần.
- Ứng dụng di động native. Web responsive là đủ.

---

## 2. Quy tắc nghiệp vụ

Mỗi quy tắc có mã để viết test và trích dẫn trong báo cáo.

### 2.1. Tài khoản và hồ sơ
- **BR-01** Đăng ký chỉ tạo `TAI_KHOAN`, vai trò `benh_nhan`. `ten_dang_nhap` không trùng, mật khẩu băm bằng BCrypt.
- **BR-02** Thông tin liên lạc (số điện thoại, email) nằm ở `TAI_KHOAN`. Đây là người đại diện nhận thông báo.
- **BR-03** Một tài khoản có thể có nhiều `BENH_NHAN`. Hồ sơ **chỉ được tạo khi đặt lịch** cho một người khám mới. Không trùng bộ (`ma_tai_khoan`, `ho_ten`, `ngay_sinh`).
- **BR-04** Tài khoản nhân viên và bác sĩ do chủ phòng khám tạo. `NHAN_VIEN` và `BAC_SI` quan hệ 1-1 với `TAI_KHOAN`. Chủ phòng khám chỉ có `TAI_KHOAN` (`vai_tro = chu_phong_kham`).
- **BR-05** Tài khoản bị khóa (`trang_thai = khoa`) không đăng nhập được.

### 2.2. Lịch làm việc
- **BR-10** Mỗi bác sĩ có **tối đa một dòng `LICH_LAM_VIEC` mỗi ngày** (UNIQUE `ma_bac_si`, `ngay`), gồm `gio_bat_dau` và `gio_ket_thuc`, trạng thái `lam_viec` hoặc `nghi`.
- **BR-11** Giờ luôn tròn **:00 hoặc :30**, và giờ kết thúc lớn hơn giờ bắt đầu.
- **BR-12** Quy ước hệ thống (cấu hình trong `application.yml`): **khung khám 30 phút**, **nghỉ trưa chung 11:30–13:30**.
- **BR-13** Sắp lịch tuần có chức năng **"Sao chép tuần trước"**: chép các dòng `lam_viec`, cộng `ngay` thêm 7 ngày, bỏ qua ngày đã có dòng.
- **BR-14** Tùy chọn: job chạy mỗi Chủ nhật tự sao chép lịch tuần hiện tại sang tuần sau.
- **BR-15** Chuyển một buổi sang `nghi`, hoặc rút ngắn giờ: hệ thống trả về **danh sách lịch hẹn bị ảnh hưởng** để nhân viên hủy và gửi thông báo.
- **BR-16** Không xóa được buổi đang có lịch hẹn (khóa ngoại chặn). Phải chuyển sang `nghi`.

### 2.3. Đặt lịch
- **BR-20** Khung trống của bác sĩ B ngày D:
  1. Lấy dòng `LICH_LAM_VIEC` của (B, D) có `trang_thai = lam_viec`.
  2. Cắt `[gio_bat_dau, gio_ket_thuc)` thành các khung 30 phút, bỏ khung trong giờ nghỉ trưa.
  3. Bỏ các khung đã có `LICH_HEN` cùng `ma_lich` và `gio_hen` mà `trang_thai <> da_huy`.
  4. Nếu D là hôm nay, bỏ thêm các khung đã qua giờ.
- **BR-21** **Một khung chỉ có một lịch còn hiệu lực**: UNIQUE (`ma_lich`, `gio_hen`, `con_hieu_luc`), trong đó `con_hieu_luc` là cột sinh = 1 khi lịch còn hiệu lực, NULL khi đã hủy (MySQL không có UNIQUE trên cột sinh `con_hieu_luc`). Khi hai người cùng đặt một khung, người sau nhận lỗi 409: "Khung giờ vừa có người đặt".
- **BR-22** `gio_hen` phải nằm trong buổi làm việc và ngoài giờ nghỉ trưa. Kiểm tra trong service.
- **BR-23** Một bệnh nhân không có hai lịch còn hiệu lực trùng ngày và giờ, kể cả với bác sĩ khác. Kiểm tra trong service.
- **BR-24** Đặt lịch là **một transaction**: tạo mới hoặc cập nhật `BENH_NHAN` và `TIEN_SU_BENH`, tạo `LICH_HEN` (`da_dat`), tạo các `CHI_TIET_LICH_HEN` (`dang_ky`) cho dịch vụ bệnh nhân chọn. Bệnh nhân **không bắt buộc** chọn dịch vụ, có thể chỉ ghi `ly_do_kham`.
- **BR-25** Chỉ đặt được trong khoảng ngày đã có lịch làm việc. Khoảng tối đa cấu hình, ví dụ 14 ngày.
- **BR-26** Bệnh nhân chỉ được tự hủy lịch đang `da_dat`, trước giờ khám. Lịch hủy giữ nguyên dòng, không xóa.

### 2.4. Tiếp nhận và khám
- **BR-30** Nhân viên "Tạo hồ sơ khám" cho lịch `da_dat` của hôm nay:
  - tạo `HO_SO_KHAM` (1-1 với lịch, `ma_nhan_vien` là người tiếp nhận);
  - `so_thu_tu` bằng số hồ sơ khám của cùng buổi làm việc cộng 1;
  - lịch chuyển `da_den`.
- **BR-31** Vòng đời lịch hẹn: `da_dat → da_den → dang_kham → hoan_thanh`, nhánh phụ `da_huy` và `vang_mat`.
- **BR-32** Cuối ngày (job 23:00), lịch còn `da_dat` của hôm nay chuyển `vang_mat`.
- **BR-33** Bác sĩ cập nhật hồ sơ khám:
  - triệu chứng, chẩn đoán, nội dung điều trị;
  - `SO_DO_RANG` theo FDI 11–48, mỗi răng một dòng mỗi lần khám;
  - chốt từng `CHI_TIET_LICH_HEN`: `da_thuc_hien` hoặc `khong_thuc_hien`. Bác sĩ có thể **thêm dịch vụ phát sinh**;
  - bổ sung `TIEN_SU_BENH`.
- **BR-34** **Bác sĩ là người quyết định dịch vụ cuối cùng.** Dịch vụ bệnh nhân đăng ký chỉ là gợi ý.

### 2.5. Lộ trình và tái khám
- **BR-40** Bác sĩ lập `LO_TRINH_DIEU_TRI` gồm các `BUOC_DIEU_TRI` (`thu_tu` không trùng trong một lộ trình, dịch vụ dự kiến, `ngay_du_kien`).
- **BR-41** Bước được thực hiện qua một dòng `CHI_TIET_LICH_HEN` có `ma_buoc`. Khi dòng đó chuyển `da_thuc_hien` thì bước chuyển `da_thuc_hien`. Một lần khám có thể làm nhiều bước.
- **BR-42** Bước gắn vào lịch hẹn phải **thuộc lộ trình của chính bệnh nhân đó**. Kiểm tra trong service.
- **BR-43** Job hằng ngày tạo `THONG_BAO` loại `nhac_tai_kham` khi đủ các điều kiện:
  - bước `chua_thuc_hien`;
  - `ngay_du_kien` trong 3 ngày tới;
  - chưa có lịch còn hiệu lực cho bước;
  - chưa nhắc trong 3 ngày gần đây.
- **BR-44** Đặt lịch tái khám từ một bước: hệ thống tạo `LICH_HEN` (`tai_kham`) và một `CHI_TIET_LICH_HEN` gồm dịch vụ của bước, `ma_buoc`, `dang_ky`. Bác sĩ mặc định là bác sĩ lập lộ trình, bệnh nhân đổi được.
- **BR-45** Bước cuối xong thì lộ trình chuyển `hoan_thanh`.

### 2.6. Thông báo
- **BR-50** `THONG_BAO` có **đúng một** trong hai cột (CHECK):
  - `huy_lich` thì có `ma_lich_hen`;
  - `nhac_tai_kham` thì có `ma_buoc`.
- **BR-51** Người nhận được suy ra qua lịch hẹn (hủy lịch) hoặc qua bước, lộ trình (nhắc tái khám), rồi tới bệnh nhân và tài khoản.

### 2.7. Trợ lý ảo
- **BR-60** Chỉ nhân viên, bác sĩ và chủ phòng khám dùng trợ lý.
- **BR-61** Phạm vi tài liệu theo vai trò:
  - nhân viên: `bang_gia`, `chinh_sach`, `quy_trinh`;
  - bác sĩ và chủ phòng khám: tất cả.
- **BR-62** Câu trả lời tra cứu **chỉ dựa trên đoạn tài liệu tìm được**, có trích dẫn. Nếu không đủ thông tin thì trả lời "Không tìm thấy trong tài liệu nội bộ". Mỗi nguồn dùng tới tạo một dòng `TRICH_DAN`.
- **BR-63** Tóm tắt hồ sơ và phân tích triệu chứng gắn `HOI_THOAI.ma_benh_nhan`. Kết quả phân tích luôn kèm câu "Gợi ý chỉ mang tính tham khảo, bác sĩ là người quyết định".

---

## 3. Yêu cầu phi chức năng

- **Bảo mật:** mật khẩu băm BCrypt; JWT (access 30 phút, refresh 7 ngày); phân quyền theo vai trò ở tầng API; mọi API của bệnh nhân kiểm tra quyền sở hữu dữ liệu; khóa API Gemini và mật khẩu CSDL đặt trong biến môi trường.
- **Toàn vẹn dữ liệu:** ràng buộc đặt trong CSDL khi có thể (UNIQUE, CHECK, FK, UNIQUE trên cột sinh `con_hieu_luc`); thao tác ghi nhiều bảng chạy trong một transaction; không xóa dữ liệu y tế, chỉ đổi trạng thái.
- **Đồng thời:** hai người đặt cùng một khung giờ thì chỉ một người thành công (unique index), người còn lại nhận lỗi 409.
- **Hiệu năng:** các thao tác nghiệp vụ phản hồi dưới 2 giây với vài nghìn bản ghi; trợ lý AI chấp nhận vài giây, giao diện có trạng thái đang xử lý.
- **Khả dụng:** giao diện tiếng Việt, responsive (máy tính tại quầy, máy tính bảng tại ghế khám).
- **Độ tin cậy của AI:** câu trả lời tra cứu chỉ dựa trên tài liệu nội bộ, có trích dẫn, không đủ thông tin thì từ chối trả lời; gợi ý lâm sàng luôn kèm cảnh báo chỉ để tham khảo.
- **Triển khai:** chạy trực tiếp trên máy (MySQL, Qdrant, backend, frontend) theo hướng dẫn trong README; có thể đóng gói Docker sau nếu cần demo trên máy khác.

## 4. Trợ lý ảo (RAG)

### 4.1. Nạp tài liệu (khi tải lên)
1. Chủ phòng khám hoặc nhân viên tải lên file PDF hoặc DOCX, chọn `loai_tai_lieu`. Hệ thống lưu file vào `storage/tai-lieu/` và tạo `TAI_LIEU`.
2. Trích văn bản bằng PDFBox hoặc POI, giữ số trang và tiêu đề mục.
3. **Chia đoạn theo cấu trúc** (mục, bước), mỗi đoạn khoảng 300–600 từ, chồng lấn khoảng 50 từ.
4. Gọi Gemini embedding cho từng đoạn.
5. Lưu vào Qdrant: vector cùng payload `{ma_tai_lieu, ten_tai_lieu, loai_tai_lieu, vi_tri: "Trang 2 – Mục 3", noi_dung}`.
6. Khi xóa hoặc thay tài liệu: xóa các điểm trong Qdrant theo `ma_tai_lieu`. Tài liệu đã được trích dẫn thì không xóa dòng `TAI_LIEU`, vì khóa ngoại chặn. Có thể bổ sung trạng thái ẩn sau.

### 4.2. Tra cứu
1. Lưu câu hỏi vào `TIN_NHAN` (`nguoi_dung`).
2. Embedding câu hỏi, tìm top-k = 5 trong Qdrant, lọc `loai_tai_lieu` theo vai trò (BR-61).
3. Tạo prompt gồm các đoạn đánh số `[1]`…`[5]`, cùng yêu cầu: trả lời bằng tiếng Việt, **chỉ dựa trên đoạn được cung cấp**, ghi số nguồn sau mỗi ý, thiếu thông tin thì nói không tìm thấy.
4. Gọi Gemini, lưu câu trả lời vào `TIN_NHAN` (`tro_ly`). Đọc các số `[n]` đã dùng, mỗi số tạo một dòng `TRICH_DAN` (`ma_tai_lieu`, `vi_tri`).
5. Giao diện hiện câu trả lời kèm các thẻ nguồn. Bấm vào thẻ thì mở file gốc.

### 4.3. Tóm tắt hồ sơ khám
Gom dữ liệu của bệnh nhân (tiền sử, các hồ sơ khám, sơ đồ răng lần gần nhất, lộ trình đang chạy), đưa vào prompt và yêu cầu tóm tắt theo mục: Tiền sử và cảnh báo, Các lần khám gần đây, Tình trạng răng, Lộ trình đang thực hiện.

### 4.4. Phân tích triệu chứng
Đầu vào gồm triệu chứng do bác sĩ nhập, tiền sử, và (tùy chọn) các đoạn phác đồ tìm được trong Qdrant. Đầu ra là các khả năng chẩn đoán, gợi ý cần kiểm tra thêm, lưu ý theo tiền sử (ví dụ dị ứng), kèm câu cảnh báo ở BR-63.

### 4.5. Đánh giá
- Bộ 30–50 câu hỏi kèm đáp án và tài liệu nguồn, lưu ở `docs/rag-eval.csv`.
- Đo **recall@5**: đoạn đúng có nằm trong 5 đoạn tìm được không. Đo **độ đúng câu trả lời** và **trích dẫn đúng nguồn** (chấm thủ công theo thang 0–2).
- Kết quả đưa vào chương đánh giá của báo cáo.

---

## 5. Thiết kế API (REST, tiền tố `/api`)

| Nhóm | Phương thức và đường dẫn | Ai | Ghi chú |
|---|---|---|---|
| Xác thực | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `GET /auth/me` | Mọi người | Refresh token nằm trong cookie HttpOnly `refresh_token` (SameSite=Strict, path `/api/auth`), không có trong body |
| Tài khoản | `PUT /me`, `PUT /me/mat-khau` | Đã đăng nhập | |
| Công khai | `GET /public/bac-si`, `GET /public/bac-si/{id}`, `GET /public/dich-vu` | Khách | |
| Quản lý nhân sự | `GET/POST /admin/nhan-vien`, `GET/POST /admin/bac-si`, `PUT .../{id}`, `PATCH /admin/tai-khoan/{id}/khoa` | Chủ PK | Tạo tài khoản và hồ sơ trong một transaction |
| Dịch vụ | `POST /dich-vu` (NV, Chủ PK), `PUT /dich-vu/{id}` (Chủ PK) | | |
| Lịch làm việc | `GET /lich-lam-viec?tuNgay&denNgay&maBacSi`, `PUT /lich-lam-viec/tuan` (lưu lưới), `POST /lich-lam-viec/sao-chep-tuan`, `PATCH /lich-lam-viec/{id}` | Chủ PK (BS xem) | PATCH trả về danh sách lịch hẹn bị ảnh hưởng |
| Khung trống | `GET /dat-lich/khung-trong?ngay=2026-11-23[&maBacSi]` | Bệnh nhân | Trả về `[{maBacSi, hoTen, maLich, khung:[{gio, trong}]}]` |
| Đặt lịch | `GET /me/benh-nhan`, `POST /lich-hen`, `GET /me/lich-hen`, `PATCH /lich-hen/{id}/huy` | Bệnh nhân | POST gồm `maBenhNhan` hoặc `benhNhanMoi`, `tienSu[]`, `maLich`, `gioHen`, `lyDoKham`, `dichVuDangKy[]` |
| Tái khám | `GET /me/buoc-can-tai-kham`, `POST /lich-hen/tai-kham` | Bệnh nhân | |
| Tiếp nhận | `GET /tiep-nhan?ngay`, `POST /ho-so-kham` (`maLichHen`), `PATCH /lich-hen/{id}/huy-boi-nhan-vien` | Nhân viên | Hủy lịch sinh `THONG_BAO` |
| Khám | `GET /bac-si/me/hang-cho?ngay`, `PATCH /lich-hen/{id}/trang-thai`, `PUT /ho-so-kham/{id}`, `PUT /ho-so-kham/{id}/so-do-rang`, `PUT /lich-hen/{id}/chi-tiet`, `POST /benh-nhan/{id}/tien-su` | Bác sĩ | |
| Lộ trình | `POST /lo-trinh`, `PUT /lo-trinh/{id}`, `POST /lo-trinh/{id}/buoc`, `PUT /buoc/{id}` | Bác sĩ | |
| Hồ sơ | `GET /benh-nhan/{id}/ho-so` | BN (của mình), NV, BS | Gồm thông tin, tiền sử, các lần khám, sơ đồ răng, lộ trình |
| Thông báo | `GET /me/thong-bao`, `PATCH /thong-bao/{id}/da-doc` | Bệnh nhân | |
| Tài liệu | `GET /tai-lieu`, `POST /tai-lieu` (multipart), `DELETE /tai-lieu/{id}`, `GET /tai-lieu/{id}/file` | NV, Chủ PK (BS xem) | |
| Trợ lý | `GET /tro-ly/hoi-thoai`, `POST /tro-ly/hoi-thoai`, `GET /tro-ly/hoi-thoai/{id}/tin-nhan`, `POST /tro-ly/hoi-thoai/{id}/tin-nhan` | NV, BS, Chủ PK | Trả câu trả lời kèm `trichDan[]` |

**Định dạng response chung:** mọi response đều có dạng `{ "code": "...", "message": "...", "result": ... }`.
- Thành công: `code = "SUCCESS"`, `result` là dữ liệu (đối tượng/mảng), bỏ qua nếu không có dữ liệu trả về. Controller tự trả `ApiResponse.success(...)`.
- Lỗi: `code` là mã trong enum `ResponseCode` (vd `VALIDATION_ERROR`, `USERNAME_ALREADY_EXISTS`), `message` là thông báo; lỗi validate đặt chi tiết từng trường trong `result` (`{ "tenDangNhap": "..." }`), các lỗi khác không có `result`.
- Mã HTTP: 400 dữ liệu sai, 401, 403, 404, 409 xung đột. Mã lỗi nghiệp vụ mới (vd khung giờ đã đặt) thêm vào `ResponseCode`.

**Job định kỳ** (`@Scheduled`):
- 23:00 mỗi ngày: chuyển `vang_mat` (BR-32).
- 07:00 mỗi ngày: nhắc tái khám (BR-43).
- Tùy chọn, Chủ nhật 20:00: sao chép lịch tuần (BR-14).

---

## 6. Màn hình frontend

| Vai trò | Màn hình |
|---|---|
| Công khai | Trang chủ, Danh sách bác sĩ, Chi tiết bác sĩ, Dịch vụ và bảng giá, Đăng ký, Đăng nhập |
| Bệnh nhân | **Đặt lịch** (chọn người khám, khai tiền sử, lý do, dịch vụ tùy chọn, chọn ngày, các thẻ bác sĩ kèm nút giờ, xác nhận) · Lịch khám của tôi · Hồ sơ khám bệnh (dòng thời gian, sơ đồ răng, lộ trình) · Thông báo (biểu tượng chuông) · Thông tin tài khoản |
| Nhân viên | Tiếp nhận hôm nay (danh sách lịch, nút Tạo hồ sơ khám) · Tra cứu bệnh nhân · Hủy lịch do bác sĩ nghỉ · Dịch vụ · Tài liệu · Trợ lý |
| Bác sĩ | Hàng chờ hôm nay · **Màn hình khám** (thông tin và cảnh báo tiền sử, lý do khám, form khám, sơ đồ răng SVG, danh sách dịch vụ đã làm, lập lộ trình, khung trợ lý bên phải) · Lịch làm việc · Trợ lý |
| Chủ phòng khám | **Sắp lịch tuần** (lưới bác sĩ × ngày, sao chép tuần trước, bật nghỉ) · Nhân viên, bác sĩ · Dịch vụ · Tài liệu · Trợ lý |

---

## 7. Kịch bản demo
1. Chủ phòng khám sao chép lịch tuần, cho bác sĩ A nghỉ một ngày.
2. Bệnh nhân đăng ký, đặt lịch cho mình (khai dị ứng Penicillin) và cho con, chọn bác sĩ B lúc 9:00.
3. Hai trình duyệt cùng đặt một khung: một bên nhận lỗi "vừa có người đặt".
4. Nhân viên tiếp nhận và tạo hồ sơ khám. Bác sĩ thấy cảnh báo dị ứng, khám, đánh dấu sơ đồ răng, thêm dịch vụ, lập lộ trình 3 bước và làm luôn bước 1.
5. Bác sĩ hỏi trợ lý "Chính sách bảo hành bọc sứ?" và nhận câu trả lời có trích dẫn. Sau đó nhờ trợ lý tóm tắt hồ sơ.
6. Chạy job nhắc tái khám. Bệnh nhân nhận thông báo, đặt lịch tái khám bước 2.
7. Bệnh nhân xem hồ sơ khám bệnh: các lần khám, sơ đồ răng, tiến độ lộ trình.
