# Thiết kế CSDL chi tiết – Phòng khám nha khoa (MySQL 8)

> Nguồn chuẩn: `database/PhongKhamNhaKhoa_mysql.sql` (tạo bảng) và `database/PhongKhamNhaKhoa_seed_mysql.sql` (dữ liệu mẫu). Cả hai đã chạy thử trên MySQL 8.0. File này sinh cùng nguồn với script: **18 bảng, 25 quan hệ**.
> Tài liệu cũ `phan-tich-do-an.md` và `actors-usecases.md` đã lỗi thời về CSDL: khi mâu thuẫn, theo file này.

## Quy ước
- Database `phong_kham_nha_khoa`, InnoDB, `utf8mb4_unicode_ci` (lưu tiếng Việt có dấu).
- Tên bảng IN_HOA, tên cột snake_case tiếng Việt không dấu; khóa chính `ma_...` INT AUTO_INCREMENT.
- Ký hiệu ràng buộc: PK khóa chính, FK → bảng, UNIQUE duy nhất, NULL được để trống, NOT NULL bắt buộc.
- Giá trị CHECK dạng liệt kê là chuỗi chữ thường, map sang enum Java (`@Enumerated(EnumType.STRING)`).
- Mỗi cột có COMMENT trong MySQL (chính là cột Mô tả bên dưới).

## Người dùng

### TAI_KHOAN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_tai_khoan` | INT | PK, AUTO_INCREMENT | Mã tài khoản (tự tăng) |
| `ten_dang_nhap` | VARCHAR(50) | NOT NULL, UNIQUE | Tên đăng nhập, không trùng |
| `mat_khau` | VARCHAR(255) | NOT NULL | Mật khẩu đã mã hóa |
| `so_dien_thoai` | VARCHAR(15) | NOT NULL | Số điện thoại liên lạc của chủ tài khoản |
| `email` | VARCHAR(100) | NULL | Email nhận thông báo, khôi phục mật khẩu |
| `vai_tro` | VARCHAR(20) | NOT NULL, CHECK (benh_nhan, nhan_vien, bac_si, chu_phong_kham) | Vai trò của tài khoản |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'hoat_dong', CHECK (hoat_dong, khoa) | Trạng thái tài khoản |
| `ngay_tao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo tài khoản |

### NHAN_VIEN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_nhan_vien` | INT | PK, AUTO_INCREMENT | Mã nhân viên (tự tăng) |
| `ma_tai_khoan` | INT | FK → TAI_KHOAN, NOT NULL, UNIQUE | Tài khoản đăng nhập của nhân viên |
| `ho_ten` | VARCHAR(100) | NOT NULL | Họ tên nhân viên |
| `ngay_vao_lam` | DATE | NULL | Ngày bắt đầu làm việc |
| `trang_thai_lam_viec` | VARCHAR(20) | NOT NULL, DEFAULT 'dang_lam', CHECK (dang_lam, nghi_viec) | Trạng thái làm việc |

### BAC_SI

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_bac_si` | INT | PK, AUTO_INCREMENT | Mã bác sĩ (tự tăng) |
| `ma_tai_khoan` | INT | FK → TAI_KHOAN, NOT NULL, UNIQUE | Tài khoản đăng nhập của bác sĩ |
| `ho_ten` | VARCHAR(100) | NOT NULL | Họ tên bác sĩ |
| `chuyen_mon` | VARCHAR(100) | NULL | Chuyên môn (chỉnh nha, phục hình, implant…) |
| `bang_cap` | VARCHAR(255) | NULL | Bằng cấp, chứng chỉ |
| `gioi_thieu` | TEXT | NULL | Giới thiệu, kinh nghiệm (hiển thị khi khách xem thông tin bác sĩ) |
| `anh_dai_dien` | VARCHAR(255) | NULL | Đường dẫn ảnh đại diện |
| `trang_thai_lam_viec` | VARCHAR(20) | NOT NULL, DEFAULT 'dang_lam', CHECK (dang_lam, nghi_viec) | Trạng thái làm việc |

### BENH_NHAN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_benh_nhan` | INT | PK, AUTO_INCREMENT | Mã bệnh nhân (tự tăng) |
| `ma_tai_khoan` | INT | FK → TAI_KHOAN, NOT NULL | Tài khoản đặt lịch và quản lý hồ sơ (một tài khoản có thể có nhiều hồ sơ) |
| `ho_ten` | VARCHAR(100) | NOT NULL | Họ tên người khám |
| `ngay_sinh` | DATE | NOT NULL, UNIQUE cùng ma_tai_khoan, ho_ten | Ngày sinh |
| `gioi_tinh` | VARCHAR(10) | NULL | Giới tính |
| `dia_chi` | VARCHAR(255) | NULL | Địa chỉ |
| `ngay_tao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo hồ sơ (sinh ra khi đặt lịch cho người khám mới) |

### TIEN_SU_BENH

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_tien_su` | INT | PK, AUTO_INCREMENT | Mã tiền sử (tự tăng) |
| `ma_benh_nhan` | INT | FK → BENH_NHAN, NOT NULL | Bệnh nhân có tiền sử |
| `loai_tien_su` | VARCHAR(20) | NOT NULL, CHECK (benh_nen, di_ung, thuoc_dang_dung, can_thiep_rang) | Bệnh nền, dị ứng, thuốc đang dùng hoặc can thiệp nha khoa trước đây |
| `ten_tien_su` | VARCHAR(150) | NOT NULL | Tên bệnh, chất gây dị ứng, tên thuốc… |
| `ghi_chu` | VARCHAR(255) | NULL | Ghi chú thêm |
| `ngay_ghi_nhan` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm ghi nhận |

## Danh mục

### DICH_VU

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_dich_vu` | INT | PK, AUTO_INCREMENT | Mã dịch vụ (tự tăng) |
| `ten_dich_vu` | VARCHAR(150) | NOT NULL | Tên dịch vụ |
| `mo_ta` | TEXT | NULL | Mô tả dịch vụ |
| `gia` | DECIMAL(12,0) | NOT NULL, CHECK (gia >= 0) | Giá dịch vụ (VNĐ) |
| `thoi_luong` | INT | NULL, CHECK (thoi_luong > 0) | Thời lượng dự kiến (phút), để tham khảo |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'kich_hoat', CHECK (kich_hoat, ngung) | Trạng thái áp dụng |

## Lịch làm việc và đặt lịch

### LICH_LAM_VIEC

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_lich` | INT | PK, AUTO_INCREMENT | Mã lịch làm việc (tự tăng) |
| `ma_bac_si` | INT | FK → BAC_SI, NOT NULL | Bác sĩ làm việc |
| `ngay` | DATE | NOT NULL, UNIQUE cùng ma_bac_si | Ngày làm việc (mỗi bác sĩ một dòng mỗi ngày) |
| `gio_bat_dau` | TIME | NOT NULL, CHECK mốc :00 hoặc :30 | Giờ bắt đầu làm việc trong ngày |
| `gio_ket_thuc` | TIME | NOT NULL, CHECK (gio_ket_thuc > gio_bat_dau), CHECK mốc :00 hoặc :30 | Giờ kết thúc (cắt khung 30 phút, bỏ giờ nghỉ trưa chung) |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'lam_viec', CHECK (lam_viec, nghi) | Làm việc hoặc nghỉ (không nhận đặt lịch) |
| `ghi_chu` | VARCHAR(255) | NULL | Lý do nghỉ, ghi chú buổi làm thêm |
| `ngay_tao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tạo |

### LICH_HEN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_lich_hen` | INT | PK, AUTO_INCREMENT | Mã lịch hẹn (tự tăng) |
| `ma_benh_nhan` | INT | FK → BENH_NHAN, NOT NULL | Người khám |
| `ma_lich` | INT | FK → LICH_LAM_VIEC, NOT NULL | Buổi làm việc được đặt (cho biết bác sĩ khám và ngày khám) |
| `gio_hen` | TIME | NOT NULL, CHECK mốc :00 hoặc :30, UNIQUE cùng ma_lich (trừ lịch đã hủy) | Giờ bắt đầu khung khám 30 phút trong buổi làm việc (giữ chỗ, bệnh nhân đến trong khung này) |
| `ly_do_kham` | VARCHAR(500) | NULL | Bệnh nhân mô tả vấn đề khi đặt lịch |
| `loai_lich_hen` | VARCHAR(20) | NOT NULL, DEFAULT 'kham_moi', CHECK (kham_moi, tai_kham) | Khám mới hoặc tái khám |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'da_dat', CHECK (da_dat, da_den, dang_kham, hoan_thanh, vang_mat, da_huy) | Trạng thái lịch hẹn |
| `ghi_chu` | TEXT | NULL | Ghi chú |
| `ngay_tao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm đặt lịch |
| `con_hieu_luc` | TINYINT (cột sinh, STORED) | = 1 khi `trang_thai <> 'da_huy'`, NULL khi đã hủy | Cột kỹ thuật cho ràng buộc chống đặt trùng khung (không có trên ERD) |

### CHI_TIET_LICH_HEN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_chi_tiet` | INT | PK, AUTO_INCREMENT | Mã chi tiết (tự tăng) |
| `ma_lich_hen` | INT | FK → LICH_HEN, NOT NULL | Lịch hẹn chứa dịch vụ |
| `ma_dich_vu` | INT | FK → DICH_VU, NOT NULL | Dịch vụ đăng ký hoặc thực hiện |
| `ma_buoc` | INT | FK → BUOC_DIEU_TRI, NULL | Bước điều trị mà dịch vụ này thực hiện (nếu thuộc lộ trình) |
| `so_rang` | TINYINT UNSIGNED | NULL, CHECK (11–48) | Răng thực hiện (nếu có) |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'dang_ky', CHECK (dang_ky, da_thuc_hien, khong_thuc_hien) | Đăng ký, đã thực hiện hoặc không thực hiện (bác sĩ quyết định) |
| `ghi_chu` | VARCHAR(255) | NULL | Ghi chú, lý do không thực hiện |

## Khám bệnh

### HO_SO_KHAM

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_ho_so_kham` | INT | PK, AUTO_INCREMENT | Mã hồ sơ khám (tự tăng) |
| `ma_lich_hen` | INT | FK → LICH_HEN, NOT NULL, UNIQUE | Lượt khám tương ứng |
| `ma_nhan_vien` | INT | FK → NHAN_VIEN, NOT NULL | Nhân viên tiếp nhận, tạo hồ sơ khám |
| `ngay_kham` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời điểm tiếp nhận |
| `so_thu_tu` | INT | NOT NULL | Số thứ tự trong hàng chờ của bác sĩ |
| `trieu_chung` | TEXT | NULL | Triệu chứng (bác sĩ ghi) |
| `chan_doan` | TEXT | NULL | Chẩn đoán |
| `noi_dung_dieu_tri` | TEXT | NULL | Nội dung điều trị đã thực hiện |
| `ghi_chu` | TEXT | NULL | Ghi chú, dặn dò bệnh nhân |

### SO_DO_RANG

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_so_do_rang` | INT | PK, AUTO_INCREMENT | Mã bản ghi sơ đồ răng (tự tăng) |
| `ma_ho_so_kham` | INT | FK → HO_SO_KHAM, NOT NULL | Lần khám ghi nhận |
| `so_rang` | TINYINT UNSIGNED | NOT NULL, CHECK (11–48), UNIQUE cùng ma_ho_so_kham | Số răng theo ký hiệu FDI |
| `tinh_trang` | VARCHAR(30) | NOT NULL, CHECK (binh_thuong, sau_rang, da_tram, mat_rang, boc_su, implant, can_nho) | Tình trạng răng |
| `ghi_chu` | TEXT | NULL | Ghi chú |

## Lộ trình điều trị

### LO_TRINH_DIEU_TRI

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_lo_trinh` | INT | PK, AUTO_INCREMENT | Mã lộ trình (tự tăng) |
| `ma_benh_nhan` | INT | FK → BENH_NHAN, NOT NULL | Bệnh nhân được điều trị |
| `ma_bac_si` | INT | FK → BAC_SI, NOT NULL | Bác sĩ lập lộ trình |
| `ten_lo_trinh` | VARCHAR(150) | NOT NULL | Tên lộ trình |
| `ngay_bat_dau` | DATE | NOT NULL | Ngày bắt đầu |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'dang_thuc_hien', CHECK (dang_thuc_hien, tam_dung, hoan_thanh) | Trạng thái lộ trình |

### BUOC_DIEU_TRI

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_buoc` | INT | PK, AUTO_INCREMENT | Mã bước điều trị (tự tăng) |
| `ma_lo_trinh` | INT | FK → LO_TRINH_DIEU_TRI, NOT NULL | Lộ trình chứa bước này |
| `ma_dich_vu` | INT | FK → DICH_VU, NULL | Dịch vụ dự kiến thực hiện ở bước này |
| `thu_tu` | INT | NOT NULL, UNIQUE cùng ma_lo_trinh | Thứ tự bước trong lộ trình |
| `noi_dung` | TEXT | NOT NULL | Nội dung bước điều trị |
| `ngay_du_kien` | DATE | NULL | Ngày tái khám dự kiến, căn cứ để hệ thống gửi nhắc |
| `trang_thai` | VARCHAR(20) | NOT NULL, DEFAULT 'chua_thuc_hien', CHECK (chua_thuc_hien, da_thuc_hien, huy) | Trạng thái bước |

## Thông báo

### THONG_BAO

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_thong_bao` | INT | PK, AUTO_INCREMENT | Mã thông báo (tự tăng) |
| `ma_lich_hen` | INT | FK → LICH_HEN, NULL | Lịch hẹn bị hủy (thông báo hủy lịch); người nhận lấy qua lịch hẹn |
| `ma_buoc` | INT | FK → BUOC_DIEU_TRI, NULL | Bước sắp đến hạn (nhắc tái khám); người nhận lấy qua lộ trình |
| `loai_thong_bao` | VARCHAR(30) | NOT NULL, CHECK (huy_lich, nhac_tai_kham) | Loại thông báo |
| `noi_dung` | TEXT | NOT NULL | Nội dung thông báo |
| `da_doc` | TINYINT(1) | NOT NULL, DEFAULT 0 | Đã đọc hay chưa |
| `thoi_gian_gui` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian gửi |

## Trợ lý ảo

### HOI_THOAI

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_hoi_thoai` | INT | PK, AUTO_INCREMENT | Mã hội thoại (tự tăng) |
| `ma_tai_khoan` | INT | FK → TAI_KHOAN, NOT NULL | Người dùng trợ lý (nhân viên, bác sĩ hoặc chủ phòng khám) |
| `ma_benh_nhan` | INT | FK → BENH_NHAN, NULL | Bệnh nhân liên quan (khi tóm tắt hồ sơ, phân tích triệu chứng) |
| `loai_chuc_nang` | VARCHAR(30) | NOT NULL, CHECK (tra_cuu_tai_lieu, tom_tat_ho_so, phan_tich_trieu_chung) | Chức năng của trợ lý được dùng |
| `tieu_de` | VARCHAR(200) | NULL | Tiêu đề hội thoại |
| `ngay_tao` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian tạo |

### TIN_NHAN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_tin_nhan` | INT | PK, AUTO_INCREMENT | Mã tin nhắn (tự tăng) |
| `ma_hoi_thoai` | INT | FK → HOI_THOAI, NOT NULL | Hội thoại chứa tin nhắn |
| `nguoi_gui` | VARCHAR(20) | NOT NULL, CHECK (nguoi_dung, tro_ly) | Người gửi |
| `noi_dung` | TEXT | NOT NULL | Nội dung tin nhắn |
| `thoi_gian` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian gửi |

### TRICH_DAN

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_trich_dan` | INT | PK, AUTO_INCREMENT | Mã trích dẫn (tự tăng) |
| `ma_tin_nhan` | INT | FK → TIN_NHAN, NOT NULL | Tin nhắn trả lời của trợ lý |
| `ma_tai_lieu` | INT | FK → TAI_LIEU, NOT NULL | Tài liệu được trích dẫn |
| `vi_tri` | VARCHAR(100) | NULL | Vị trí trích dẫn trong tài liệu (trang, mục) |

### TAI_LIEU

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| `ma_tai_lieu` | INT | PK, AUTO_INCREMENT | Mã tài liệu (tự tăng) |
| `ma_tai_khoan` | INT | FK → TAI_KHOAN, NOT NULL | Người tải lên (nhân viên hoặc chủ phòng khám) |
| `ten_tai_lieu` | VARCHAR(200) | NOT NULL | Tên tài liệu |
| `loai_tai_lieu` | VARCHAR(30) | NOT NULL, CHECK (quy_trinh, huong_dan_ky_thuat, phac_do, bang_gia, chinh_sach) | Loại tài liệu |
| `duong_dan_file` | VARCHAR(255) | NOT NULL | Đường dẫn file |
| `ngay_cap_nhat` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Ngày cập nhật gần nhất |

## Danh sách 25 quan hệ

| # | Bảng cha (1) | Bảng con | Loại | Khóa ngoại |
|---|---|---|---|---|
| 1 | TAI_KHOAN | NHAN_VIEN | 1-1 | `NHAN_VIEN.ma_tai_khoan` |
| 2 | TAI_KHOAN | BAC_SI | 1-1 | `BAC_SI.ma_tai_khoan` |
| 3 | TAI_KHOAN | BENH_NHAN | 1-n | `BENH_NHAN.ma_tai_khoan` |
| 4 | BENH_NHAN | TIEN_SU_BENH | 1-n | `TIEN_SU_BENH.ma_benh_nhan` |
| 5 | BAC_SI | LICH_LAM_VIEC | 1-n | `LICH_LAM_VIEC.ma_bac_si` |
| 6 | BENH_NHAN | LICH_HEN | 1-n | `LICH_HEN.ma_benh_nhan` |
| 7 | LICH_LAM_VIEC | LICH_HEN | 1-n | `LICH_HEN.ma_lich` |
| 8 | LICH_HEN | CHI_TIET_LICH_HEN | 1-n | `CHI_TIET_LICH_HEN.ma_lich_hen` |
| 9 | DICH_VU | CHI_TIET_LICH_HEN | 1-n | `CHI_TIET_LICH_HEN.ma_dich_vu` |
| 10 | BUOC_DIEU_TRI | CHI_TIET_LICH_HEN | 1-n | `CHI_TIET_LICH_HEN.ma_buoc` (có thể trống) |
| 11 | LICH_HEN | HO_SO_KHAM | 1-1 | `HO_SO_KHAM.ma_lich_hen` |
| 12 | NHAN_VIEN | HO_SO_KHAM | 1-n | `HO_SO_KHAM.ma_nhan_vien` |
| 13 | HO_SO_KHAM | SO_DO_RANG | 1-n | `SO_DO_RANG.ma_ho_so_kham` |
| 14 | BENH_NHAN | LO_TRINH_DIEU_TRI | 1-n | `LO_TRINH_DIEU_TRI.ma_benh_nhan` |
| 15 | BAC_SI | LO_TRINH_DIEU_TRI | 1-n | `LO_TRINH_DIEU_TRI.ma_bac_si` |
| 16 | LO_TRINH_DIEU_TRI | BUOC_DIEU_TRI | 1-n | `BUOC_DIEU_TRI.ma_lo_trinh` |
| 17 | DICH_VU | BUOC_DIEU_TRI | 1-n | `BUOC_DIEU_TRI.ma_dich_vu` (có thể trống) |
| 18 | LICH_HEN | THONG_BAO | 1-n | `THONG_BAO.ma_lich_hen` (có thể trống) |
| 19 | BUOC_DIEU_TRI | THONG_BAO | 1-n | `THONG_BAO.ma_buoc` (có thể trống) |
| 20 | TAI_KHOAN | HOI_THOAI | 1-n | `HOI_THOAI.ma_tai_khoan` |
| 21 | BENH_NHAN | HOI_THOAI | 1-n | `HOI_THOAI.ma_benh_nhan` (có thể trống) |
| 22 | HOI_THOAI | TIN_NHAN | 1-n | `TIN_NHAN.ma_hoi_thoai` |
| 23 | TIN_NHAN | TRICH_DAN | 1-n | `TRICH_DAN.ma_tin_nhan` |
| 24 | TAI_LIEU | TRICH_DAN | 1-n | `TRICH_DAN.ma_tai_lieu` |
| 25 | TAI_KHOAN | TAI_LIEU | 1-n | `TAI_LIEU.ma_tai_khoan` |

Tổng: 25 quan hệ.

## Ràng buộc nhiều cột và ràng buộc đặc biệt

| Tên | Bảng | Nội dung |
|---|---|---|
| `UQ_BENH_NHAN_tk_ten_ngaysinh` | BENH_NHAN | UNIQUE (ma_tai_khoan, ho_ten, ngay_sinh) – không tạo trùng hồ sơ trong một tài khoản |
| `UQ_LICH_LAM_VIEC_bac_si_ngay` | LICH_LAM_VIEC | UNIQUE (ma_bac_si, ngay) – mỗi bác sĩ một dòng mỗi ngày |
| `UQ_LICH_HEN_lich_gio` | LICH_HEN | UNIQUE (ma_lich, gio_hen, con_hieu_luc) – **mỗi khung 30 phút chỉ một lịch còn hiệu lực**; lịch đã hủy có `con_hieu_luc = NULL` nên không chặn đặt lại |
| `UQ_SO_DO_RANG_ho_so_rang` | SO_DO_RANG | UNIQUE (ma_ho_so_kham, so_rang) |
| `UQ_BUOC_DIEU_TRI_thu_tu` | BUOC_DIEU_TRI | UNIQUE (ma_lo_trinh, thu_tu) |
| `CK_..._moc30` | LICH_LAM_VIEC, LICH_HEN | `MINUTE(gio) IN (0, 30) AND SECOND(gio) = 0` – giờ tròn mốc 30 phút |
| `CK_THONG_BAO_lien_ket` | THONG_BAO | `huy_lich` ⇒ có `ma_lich_hen`, không có `ma_buoc`; `nhac_tai_kham` ⇒ ngược lại |

**Vì sao có cột `con_hieu_luc`:** MySQL không hỗ trợ unique index có điều kiện (`WHERE trang_thai <> 'da_huy'`). Cách chuẩn là thêm cột sinh tự động bằng 1 khi lịch còn hiệu lực và NULL khi đã hủy, rồi đặt UNIQUE trên (ma_lich, gio_hen, con_hieu_luc). UNIQUE của MySQL cho phép nhiều giá trị NULL, nên các lịch đã hủy không chặn nhau. Cột này do MySQL tự tính, ứng dụng không ghi vào.

**Đã kiểm thử trên MySQL 8.0:** đặt trùng khung → lỗi 1062; đặt lại khung của lịch đã hủy → được; giờ 09:15 → lỗi CHECK; trạng thái sai → lỗi CHECK; thông báo hủy thiếu lịch hẹn → lỗi CHECK; bác sĩ 2 dòng cùng ngày → lỗi 1062; giờ kết thúc trước giờ bắt đầu → lỗi CHECK; xóa buổi đang có lịch hẹn → lỗi khóa ngoại 1451; số răng 99 → lỗi CHECK.

## Ràng buộc kiểm tra trong code (không làm được bằng CHECK)
- `LICH_HEN.gio_hen` nằm trong [gio_bat_dau, gio_ket_thuc) của buổi `ma_lich` và ngoài giờ nghỉ trưa 11:30–13:30; khung 30 phút.
- Một bệnh nhân không có 2 lịch còn hiệu lực trùng ngày và giờ (ngày lấy qua LICH_LAM_VIEC).
- `CHI_TIET_LICH_HEN.ma_buoc` phải thuộc lộ trình của chính bệnh nhân của lịch hẹn.
- Dòng chi tiết có `ma_buoc` chuyển `da_thuc_hien` ⇒ bước chuyển `da_thuc_hien`; bước cuối xong ⇒ lộ trình `hoan_thanh`.
- Không xóa LICH_LAM_VIEC đang có lịch hẹn (khóa ngoại chặn) ⇒ chuyển `nghi` và xử lý lịch hẹn bị ảnh hưởng.

## Chuỗi nghiệp vụ chính
```
TAI_KHOAN ─< BENH_NHAN ─< LICH_HEN >─ LICH_LAM_VIEC >─ BAC_SI
                 │            ├─< CHI_TIET_LICH_HEN >─ DICH_VU
                 │            │          └─(ma_buoc)─> BUOC_DIEU_TRI >─ LO_TRINH_DIEU_TRI
                 │            └── HO_SO_KHAM ─< SO_DO_RANG
                 └─< TIEN_SU_BENH
```

## Cách tạo CSDL (không dùng Flyway)
1. Cài MySQL 8.0 (hoặc chạy bằng Docker: `docker run -d --name mysql-nhakhoa -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:8.0`).
2. Chạy `database/PhongKhamNhaKhoa_mysql.sql` (tạo database và bảng), rồi `database/PhongKhamNhaKhoa_seed_mysql.sql` (dữ liệu mẫu) bằng MySQL Workbench hoặc dòng lệnh:
   ```bash
   mysql -u root -p --default-character-set=utf8mb4 < database/PhongKhamNhaKhoa_mysql.sql
   mysql -u root -p --default-character-set=utf8mb4 < database/PhongKhamNhaKhoa_seed_mysql.sql
   ```
3. Script tạo bảng có `DROP DATABASE IF EXISTS` ở đầu: chạy lại là làm mới toàn bộ (mất dữ liệu cũ).
4. Khi cần sửa CSDL về sau: viết file `database/thay-doi/YYYYMMDD_mo-ta.sql` chứa lệnh ALTER, chạy tay và cập nhật lại script chính.

## Lưu ý khi viết JPA (Spring Boot + MySQL)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/phong_kham_nha_khoa?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh
    username: root
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate   # chỉ kiểm tra entity khớp bảng, KHÔNG tự tạo/sửa bảng
    open-in-view: false
```
- Driver: `com.mysql:mysql-connector-j`. Không thêm Flyway/Liquibase.
- Giữ nguyên tên bảng/cột: `@Table(name="LICH_HEN")`, `@Column(name="gio_hen")`. Trên Linux, MySQL phân biệt hoa thường tên bảng, nên viết đúng IN_HOA như script.
- Ánh xạ kiểu: VARCHAR/TEXT → `String`; DECIMAL(12,0) → `BigDecimal`; TINYINT UNSIGNED → `Short`; TINYINT(1) → `Boolean`; DATE/TIME/DATETIME → `LocalDate`/`LocalTime`/`LocalDateTime`; INT AUTO_INCREMENT → `Integer` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Cột trạng thái dùng enum Java với `@Enumerated(EnumType.STRING)`, giá trị khớp CHECK (ví dụ `da_dat`).
- Cột `con_hieu_luc` do MySQL tự tính: nếu map vào entity thì đặt `@Column(insertable = false, updatable = false)`, hoặc không map.
- Quan hệ mặc định `FetchType.LAZY`; API trả DTO, không trả entity (tránh vòng lặp khi chuyển JSON).
- Bắt `DataIntegrityViolationException` có tên `UQ_LICH_HEN_lich_gio` để trả HTTP 409 "Khung giờ vừa có người đặt".
