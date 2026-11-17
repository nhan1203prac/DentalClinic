-- =====================================================================
-- CSDL phòng khám nha khoa – MySQL 8.0+ (InnoDB, utf8mb4)
-- 18 bảng, 25 quan hệ – sinh cùng nguồn với ERD
-- Chạy: mysql -u root -p < PhongKhamNhaKhoa_mysql.sql  (hoặc mở bằng MySQL Workbench)
-- =====================================================================
DROP DATABASE IF EXISTS phong_kham_nha_khoa;
CREATE DATABASE phong_kham_nha_khoa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE phong_kham_nha_khoa;

CREATE TABLE TAI_KHOAN (
    ma_tai_khoan INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã tài khoản (tự tăng)',
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên đăng nhập, không trùng',
    mat_khau VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',
    so_dien_thoai VARCHAR(15) NOT NULL COMMENT 'Số điện thoại liên lạc của chủ tài khoản',
    email VARCHAR(100) NULL COMMENT 'Email nhận thông báo, khôi phục mật khẩu',
    vai_tro VARCHAR(20) NOT NULL COMMENT 'Vai trò của tài khoản',
    CONSTRAINT CK_TAI_KHOAN_vai_tro CHECK (vai_tro IN ('benh_nhan', 'nhan_vien', 'bac_si', 'chu_phong_kham')),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'hoat_dong' COMMENT 'Trạng thái tài khoản',
    CONSTRAINT CK_TAI_KHOAN_trang_thai CHECK (trang_thai IN ('hoat_dong', 'khoa')),
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản'
) ENGINE = InnoDB;

CREATE TABLE NHAN_VIEN (
    ma_nhan_vien INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã nhân viên (tự tăng)',
    ma_tai_khoan INT NOT NULL UNIQUE COMMENT 'Tài khoản đăng nhập của nhân viên',
    ho_ten VARCHAR(100) NOT NULL COMMENT 'Họ tên nhân viên',
    ngay_vao_lam DATE NULL COMMENT 'Ngày bắt đầu làm việc',
    trang_thai_lam_viec VARCHAR(20) NOT NULL DEFAULT 'dang_lam' COMMENT 'Trạng thái làm việc',
    CONSTRAINT CK_NHAN_VIEN_trang_thai_lam_viec CHECK (trang_thai_lam_viec IN ('dang_lam', 'nghi_viec'))
) ENGINE = InnoDB;

CREATE TABLE BAC_SI (
    ma_bac_si INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã bác sĩ (tự tăng)',
    ma_tai_khoan INT NOT NULL UNIQUE COMMENT 'Tài khoản đăng nhập của bác sĩ',
    ho_ten VARCHAR(100) NOT NULL COMMENT 'Họ tên bác sĩ',
    chuyen_mon VARCHAR(100) NULL COMMENT 'Chuyên môn (chỉnh nha, phục hình, implant…)',
    bang_cap VARCHAR(255) NULL COMMENT 'Bằng cấp, chứng chỉ',
    gioi_thieu TEXT NULL COMMENT 'Giới thiệu, kinh nghiệm (hiển thị khi khách xem thông tin bác sĩ)',
    anh_dai_dien VARCHAR(255) NULL COMMENT 'Đường dẫn ảnh đại diện',
    trang_thai_lam_viec VARCHAR(20) NOT NULL DEFAULT 'dang_lam' COMMENT 'Trạng thái làm việc',
    CONSTRAINT CK_BAC_SI_trang_thai_lam_viec CHECK (trang_thai_lam_viec IN ('dang_lam', 'nghi_viec'))
) ENGINE = InnoDB;

CREATE TABLE BENH_NHAN (
    ma_benh_nhan INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã bệnh nhân (tự tăng)',
    ma_tai_khoan INT NOT NULL COMMENT 'Tài khoản đặt lịch và quản lý hồ sơ (một tài khoản có thể có nhiều hồ sơ)',
    ho_ten VARCHAR(100) NOT NULL COMMENT 'Họ tên người khám',
    ngay_sinh DATE NOT NULL COMMENT 'Ngày sinh',
    gioi_tinh VARCHAR(10) NULL COMMENT 'Giới tính',
    dia_chi VARCHAR(255) NULL COMMENT 'Địa chỉ',
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo hồ sơ (sinh ra khi đặt lịch cho người khám mới)',
    CONSTRAINT UQ_BENH_NHAN_tk_ten_ngaysinh UNIQUE (ma_tai_khoan, ho_ten, ngay_sinh)
) ENGINE = InnoDB;

CREATE TABLE TIEN_SU_BENH (
    ma_tien_su INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã tiền sử (tự tăng)',
    ma_benh_nhan INT NOT NULL COMMENT 'Bệnh nhân có tiền sử',
    loai_tien_su VARCHAR(20) NOT NULL COMMENT 'Bệnh nền, dị ứng, thuốc đang dùng hoặc can thiệp nha khoa trước đây',
    CONSTRAINT CK_TIEN_SU_BENH_loai_tien_su CHECK (loai_tien_su IN ('benh_nen', 'di_ung', 'thuoc_dang_dung', 'can_thiep_rang')),
    ten_tien_su VARCHAR(150) NOT NULL COMMENT 'Tên bệnh, chất gây dị ứng, tên thuốc…',
    ghi_chu VARCHAR(255) NULL COMMENT 'Ghi chú thêm',
    ngay_ghi_nhan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm ghi nhận'
) ENGINE = InnoDB;

CREATE TABLE DICH_VU (
    ma_dich_vu INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã dịch vụ (tự tăng)',
    ten_dich_vu VARCHAR(150) NOT NULL COMMENT 'Tên dịch vụ',
    mo_ta TEXT NULL COMMENT 'Mô tả dịch vụ',
    gia DECIMAL(12,0) NOT NULL COMMENT 'Giá dịch vụ (VNĐ)',
    CONSTRAINT CK_DICH_VU_gia CHECK (gia >= 0),
    thoi_luong INT NULL COMMENT 'Thời lượng dự kiến (phút), để tham khảo',
    CONSTRAINT CK_DICH_VU_thoi_luong CHECK (thoi_luong > 0),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'kich_hoat' COMMENT 'Trạng thái áp dụng',
    CONSTRAINT CK_DICH_VU_trang_thai CHECK (trang_thai IN ('kich_hoat', 'ngung'))
) ENGINE = InnoDB;

CREATE TABLE LICH_LAM_VIEC (
    ma_lich INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã lịch làm việc (tự tăng)',
    ma_bac_si INT NOT NULL COMMENT 'Bác sĩ làm việc',
    ngay DATE NOT NULL COMMENT 'Ngày làm việc (mỗi bác sĩ một dòng mỗi ngày)',
    gio_bat_dau TIME NOT NULL COMMENT 'Giờ bắt đầu làm việc trong ngày',
    CONSTRAINT CK_LICH_LAM_VIEC_gio_bat_dau_moc30 CHECK (MINUTE(gio_bat_dau) IN (0, 30) AND SECOND(gio_bat_dau) = 0),
    gio_ket_thuc TIME NOT NULL COMMENT 'Giờ kết thúc (cắt khung 30 phút, bỏ giờ nghỉ trưa chung)',
    CONSTRAINT CK_LICH_LAM_VIEC_gio_ket_thuc CHECK (gio_ket_thuc > gio_bat_dau),
    CONSTRAINT CK_LICH_LAM_VIEC_gio_ket_thuc_moc30 CHECK (MINUTE(gio_ket_thuc) IN (0, 30) AND SECOND(gio_ket_thuc) = 0),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'lam_viec' COMMENT 'Làm việc hoặc nghỉ (không nhận đặt lịch)',
    CONSTRAINT CK_LICH_LAM_VIEC_trang_thai CHECK (trang_thai IN ('lam_viec', 'nghi')),
    ghi_chu VARCHAR(255) NULL COMMENT 'Lý do nghỉ, ghi chú buổi làm thêm',
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo',
    CONSTRAINT UQ_LICH_LAM_VIEC_bac_si_ngay UNIQUE (ma_bac_si, ngay)
) ENGINE = InnoDB;

CREATE TABLE LICH_HEN (
    ma_lich_hen INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã lịch hẹn (tự tăng)',
    ma_benh_nhan INT NOT NULL COMMENT 'Người khám',
    ma_lich INT NOT NULL COMMENT 'Buổi làm việc được đặt (cho biết bác sĩ khám và ngày khám)',
    gio_hen TIME NOT NULL COMMENT 'Giờ bắt đầu khung khám 30 phút trong buổi làm việc (giữ chỗ, bệnh nhân đến trong khung này)',
    CONSTRAINT CK_LICH_HEN_gio_hen_moc30 CHECK (MINUTE(gio_hen) IN (0, 30) AND SECOND(gio_hen) = 0),
    ly_do_kham VARCHAR(500) NULL COMMENT 'Bệnh nhân mô tả vấn đề khi đặt lịch',
    loai_lich_hen VARCHAR(20) NOT NULL DEFAULT 'kham_moi' COMMENT 'Khám mới hoặc tái khám',
    CONSTRAINT CK_LICH_HEN_loai_lich_hen CHECK (loai_lich_hen IN ('kham_moi', 'tai_kham')),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'da_dat' COMMENT 'Trạng thái lịch hẹn',
    CONSTRAINT CK_LICH_HEN_trang_thai CHECK (trang_thai IN ('da_dat', 'da_den', 'dang_kham', 'hoan_thanh', 'vang_mat', 'da_huy')),
    ghi_chu TEXT NULL COMMENT 'Ghi chú',
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm đặt lịch',
    con_hieu_luc TINYINT GENERATED ALWAYS AS (IF(trang_thai <> 'da_huy', 1, NULL)) STORED COMMENT 'Cột kỹ thuật: 1 = lịch còn hiệu lực, NULL = đã hủy (dùng cho ràng buộc chống đặt trùng khung)',
    CONSTRAINT UQ_LICH_HEN_lich_gio UNIQUE (ma_lich, gio_hen, con_hieu_luc)
) ENGINE = InnoDB;

CREATE TABLE CHI_TIET_LICH_HEN (
    ma_chi_tiet INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã chi tiết (tự tăng)',
    ma_lich_hen INT NOT NULL COMMENT 'Lịch hẹn chứa dịch vụ',
    ma_dich_vu INT NOT NULL COMMENT 'Dịch vụ đăng ký hoặc thực hiện',
    ma_buoc INT NULL COMMENT 'Bước điều trị mà dịch vụ này thực hiện (nếu thuộc lộ trình)',
    so_rang TINYINT UNSIGNED NULL COMMENT 'Răng thực hiện (nếu có)',
    CONSTRAINT CK_CHI_TIET_LICH_HEN_so_rang CHECK (so_rang BETWEEN 11 AND 48),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'dang_ky' COMMENT 'Đăng ký, đã thực hiện hoặc không thực hiện (bác sĩ quyết định)',
    CONSTRAINT CK_CHI_TIET_LICH_HEN_trang_thai CHECK (trang_thai IN ('dang_ky', 'da_thuc_hien', 'khong_thuc_hien')),
    ghi_chu VARCHAR(255) NULL COMMENT 'Ghi chú, lý do không thực hiện'
) ENGINE = InnoDB;

CREATE TABLE HO_SO_KHAM (
    ma_ho_so_kham INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã hồ sơ khám (tự tăng)',
    ma_lich_hen INT NOT NULL UNIQUE COMMENT 'Lượt khám tương ứng',
    ma_nhan_vien INT NOT NULL COMMENT 'Nhân viên tiếp nhận, tạo hồ sơ khám',
    ngay_kham DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tiếp nhận',
    so_thu_tu INT NOT NULL COMMENT 'Số thứ tự trong hàng chờ của bác sĩ',
    trieu_chung TEXT NULL COMMENT 'Triệu chứng (bác sĩ ghi)',
    chan_doan TEXT NULL COMMENT 'Chẩn đoán',
    noi_dung_dieu_tri TEXT NULL COMMENT 'Nội dung điều trị đã thực hiện',
    ghi_chu TEXT NULL COMMENT 'Ghi chú, dặn dò bệnh nhân'
) ENGINE = InnoDB;

CREATE TABLE SO_DO_RANG (
    ma_so_do_rang INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã bản ghi sơ đồ răng (tự tăng)',
    ma_ho_so_kham INT NOT NULL COMMENT 'Lần khám ghi nhận',
    so_rang TINYINT UNSIGNED NOT NULL COMMENT 'Số răng theo ký hiệu FDI',
    CONSTRAINT CK_SO_DO_RANG_so_rang CHECK (so_rang BETWEEN 11 AND 48),
    tinh_trang VARCHAR(30) NOT NULL COMMENT 'Tình trạng răng',
    CONSTRAINT CK_SO_DO_RANG_tinh_trang CHECK (tinh_trang IN ('binh_thuong', 'sau_rang', 'da_tram', 'mat_rang', 'boc_su', 'implant', 'can_nho')),
    ghi_chu TEXT NULL COMMENT 'Ghi chú',
    CONSTRAINT UQ_SO_DO_RANG_ho_so_rang UNIQUE (ma_ho_so_kham, so_rang)
) ENGINE = InnoDB;

CREATE TABLE LO_TRINH_DIEU_TRI (
    ma_lo_trinh INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã lộ trình (tự tăng)',
    ma_benh_nhan INT NOT NULL COMMENT 'Bệnh nhân được điều trị',
    ma_bac_si INT NOT NULL COMMENT 'Bác sĩ lập lộ trình',
    ten_lo_trinh VARCHAR(150) NOT NULL COMMENT 'Tên lộ trình',
    ngay_bat_dau DATE NOT NULL COMMENT 'Ngày bắt đầu',
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'dang_thuc_hien' COMMENT 'Trạng thái lộ trình',
    CONSTRAINT CK_LO_TRINH_DIEU_TRI_trang_thai CHECK (trang_thai IN ('dang_thuc_hien', 'tam_dung', 'hoan_thanh'))
) ENGINE = InnoDB;

CREATE TABLE BUOC_DIEU_TRI (
    ma_buoc INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã bước điều trị (tự tăng)',
    ma_lo_trinh INT NOT NULL COMMENT 'Lộ trình chứa bước này',
    ma_dich_vu INT NULL COMMENT 'Dịch vụ dự kiến thực hiện ở bước này',
    thu_tu INT NOT NULL COMMENT 'Thứ tự bước trong lộ trình',
    noi_dung TEXT NOT NULL COMMENT 'Nội dung bước điều trị',
    ngay_du_kien DATE NULL COMMENT 'Ngày tái khám dự kiến, căn cứ để hệ thống gửi nhắc',
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'chua_thuc_hien' COMMENT 'Trạng thái bước',
    CONSTRAINT CK_BUOC_DIEU_TRI_trang_thai CHECK (trang_thai IN ('chua_thuc_hien', 'da_thuc_hien', 'huy')),
    CONSTRAINT UQ_BUOC_DIEU_TRI_thu_tu UNIQUE (ma_lo_trinh, thu_tu)
) ENGINE = InnoDB;

CREATE TABLE THONG_BAO (
    ma_thong_bao INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã thông báo (tự tăng)',
    ma_lich_hen INT NULL COMMENT 'Lịch hẹn bị hủy (thông báo hủy lịch); người nhận lấy qua lịch hẹn',
    ma_buoc INT NULL COMMENT 'Bước sắp đến hạn (nhắc tái khám); người nhận lấy qua lộ trình',
    loai_thong_bao VARCHAR(30) NOT NULL COMMENT 'Loại thông báo',
    CONSTRAINT CK_THONG_BAO_loai_thong_bao CHECK (loai_thong_bao IN ('huy_lich', 'nhac_tai_kham')),
    noi_dung TEXT NOT NULL COMMENT 'Nội dung thông báo',
    da_doc TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Đã đọc hay chưa',
    thoi_gian_gui DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gửi',
    CONSTRAINT CK_THONG_BAO_lien_ket CHECK ((loai_thong_bao = 'huy_lich' AND ma_lich_hen IS NOT NULL AND ma_buoc IS NULL) OR (loai_thong_bao = 'nhac_tai_kham' AND ma_buoc IS NOT NULL AND ma_lich_hen IS NULL))
) ENGINE = InnoDB;

CREATE TABLE HOI_THOAI (
    ma_hoi_thoai INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã hội thoại (tự tăng)',
    ma_tai_khoan INT NOT NULL COMMENT 'Người dùng trợ lý (nhân viên, bác sĩ hoặc chủ phòng khám)',
    ma_benh_nhan INT NULL COMMENT 'Bệnh nhân liên quan (khi tóm tắt hồ sơ, phân tích triệu chứng)',
    loai_chuc_nang VARCHAR(30) NOT NULL COMMENT 'Chức năng của trợ lý được dùng',
    CONSTRAINT CK_HOI_THOAI_loai_chuc_nang CHECK (loai_chuc_nang IN ('tra_cuu_tai_lieu', 'tom_tat_ho_so', 'phan_tich_trieu_chung')),
    tieu_de VARCHAR(200) NULL COMMENT 'Tiêu đề hội thoại',
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo'
) ENGINE = InnoDB;

CREATE TABLE TIN_NHAN (
    ma_tin_nhan INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã tin nhắn (tự tăng)',
    ma_hoi_thoai INT NOT NULL COMMENT 'Hội thoại chứa tin nhắn',
    nguoi_gui VARCHAR(20) NOT NULL COMMENT 'Người gửi',
    CONSTRAINT CK_TIN_NHAN_nguoi_gui CHECK (nguoi_gui IN ('nguoi_dung', 'tro_ly')),
    noi_dung TEXT NOT NULL COMMENT 'Nội dung tin nhắn',
    thoi_gian DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gửi'
) ENGINE = InnoDB;

CREATE TABLE TRICH_DAN (
    ma_trich_dan INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã trích dẫn (tự tăng)',
    ma_tin_nhan INT NOT NULL COMMENT 'Tin nhắn trả lời của trợ lý',
    ma_tai_lieu INT NOT NULL COMMENT 'Tài liệu được trích dẫn',
    vi_tri VARCHAR(100) NULL COMMENT 'Vị trí trích dẫn trong tài liệu (trang, mục)'
) ENGINE = InnoDB;

CREATE TABLE TAI_LIEU (
    ma_tai_lieu INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã tài liệu (tự tăng)',
    ma_tai_khoan INT NOT NULL COMMENT 'Người tải lên (nhân viên hoặc chủ phòng khám)',
    ten_tai_lieu VARCHAR(200) NOT NULL COMMENT 'Tên tài liệu',
    loai_tai_lieu VARCHAR(30) NOT NULL COMMENT 'Loại tài liệu',
    CONSTRAINT CK_TAI_LIEU_loai_tai_lieu CHECK (loai_tai_lieu IN ('quy_trinh', 'huong_dan_ky_thuat', 'phac_do', 'bang_gia', 'chinh_sach')),
    duong_dan_file VARCHAR(255) NOT NULL COMMENT 'Đường dẫn file',
    ngay_cap_nhat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày cập nhật gần nhất'
) ENGINE = InnoDB;

-- ---------- Khóa ngoại (thêm sau khi tạo đủ bảng vì có vòng tham chiếu) ----------
ALTER TABLE NHAN_VIEN ADD CONSTRAINT FK_NHAN_VIEN_ma_tai_khoan FOREIGN KEY (ma_tai_khoan) REFERENCES TAI_KHOAN(ma_tai_khoan);
ALTER TABLE BAC_SI ADD CONSTRAINT FK_BAC_SI_ma_tai_khoan FOREIGN KEY (ma_tai_khoan) REFERENCES TAI_KHOAN(ma_tai_khoan);
ALTER TABLE BENH_NHAN ADD CONSTRAINT FK_BENH_NHAN_ma_tai_khoan FOREIGN KEY (ma_tai_khoan) REFERENCES TAI_KHOAN(ma_tai_khoan);
ALTER TABLE TIEN_SU_BENH ADD CONSTRAINT FK_TIEN_SU_BENH_ma_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES BENH_NHAN(ma_benh_nhan);
ALTER TABLE LICH_LAM_VIEC ADD CONSTRAINT FK_LICH_LAM_VIEC_ma_bac_si FOREIGN KEY (ma_bac_si) REFERENCES BAC_SI(ma_bac_si);
ALTER TABLE LICH_HEN ADD CONSTRAINT FK_LICH_HEN_ma_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES BENH_NHAN(ma_benh_nhan);
ALTER TABLE LICH_HEN ADD CONSTRAINT FK_LICH_HEN_ma_lich FOREIGN KEY (ma_lich) REFERENCES LICH_LAM_VIEC(ma_lich);
ALTER TABLE CHI_TIET_LICH_HEN ADD CONSTRAINT FK_CHI_TIET_LICH_HEN_ma_lich_hen FOREIGN KEY (ma_lich_hen) REFERENCES LICH_HEN(ma_lich_hen);
ALTER TABLE CHI_TIET_LICH_HEN ADD CONSTRAINT FK_CHI_TIET_LICH_HEN_ma_dich_vu FOREIGN KEY (ma_dich_vu) REFERENCES DICH_VU(ma_dich_vu);
ALTER TABLE CHI_TIET_LICH_HEN ADD CONSTRAINT FK_CHI_TIET_LICH_HEN_ma_buoc FOREIGN KEY (ma_buoc) REFERENCES BUOC_DIEU_TRI(ma_buoc);
ALTER TABLE HO_SO_KHAM ADD CONSTRAINT FK_HO_SO_KHAM_ma_lich_hen FOREIGN KEY (ma_lich_hen) REFERENCES LICH_HEN(ma_lich_hen);
ALTER TABLE HO_SO_KHAM ADD CONSTRAINT FK_HO_SO_KHAM_ma_nhan_vien FOREIGN KEY (ma_nhan_vien) REFERENCES NHAN_VIEN(ma_nhan_vien);
ALTER TABLE SO_DO_RANG ADD CONSTRAINT FK_SO_DO_RANG_ma_ho_so_kham FOREIGN KEY (ma_ho_so_kham) REFERENCES HO_SO_KHAM(ma_ho_so_kham);
ALTER TABLE LO_TRINH_DIEU_TRI ADD CONSTRAINT FK_LO_TRINH_DIEU_TRI_ma_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES BENH_NHAN(ma_benh_nhan);
ALTER TABLE LO_TRINH_DIEU_TRI ADD CONSTRAINT FK_LO_TRINH_DIEU_TRI_ma_bac_si FOREIGN KEY (ma_bac_si) REFERENCES BAC_SI(ma_bac_si);
ALTER TABLE BUOC_DIEU_TRI ADD CONSTRAINT FK_BUOC_DIEU_TRI_ma_lo_trinh FOREIGN KEY (ma_lo_trinh) REFERENCES LO_TRINH_DIEU_TRI(ma_lo_trinh);
ALTER TABLE BUOC_DIEU_TRI ADD CONSTRAINT FK_BUOC_DIEU_TRI_ma_dich_vu FOREIGN KEY (ma_dich_vu) REFERENCES DICH_VU(ma_dich_vu);
ALTER TABLE THONG_BAO ADD CONSTRAINT FK_THONG_BAO_ma_lich_hen FOREIGN KEY (ma_lich_hen) REFERENCES LICH_HEN(ma_lich_hen);
ALTER TABLE THONG_BAO ADD CONSTRAINT FK_THONG_BAO_ma_buoc FOREIGN KEY (ma_buoc) REFERENCES BUOC_DIEU_TRI(ma_buoc);
ALTER TABLE HOI_THOAI ADD CONSTRAINT FK_HOI_THOAI_ma_tai_khoan FOREIGN KEY (ma_tai_khoan) REFERENCES TAI_KHOAN(ma_tai_khoan);
ALTER TABLE HOI_THOAI ADD CONSTRAINT FK_HOI_THOAI_ma_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES BENH_NHAN(ma_benh_nhan);
ALTER TABLE TIN_NHAN ADD CONSTRAINT FK_TIN_NHAN_ma_hoi_thoai FOREIGN KEY (ma_hoi_thoai) REFERENCES HOI_THOAI(ma_hoi_thoai);
ALTER TABLE TRICH_DAN ADD CONSTRAINT FK_TRICH_DAN_ma_tin_nhan FOREIGN KEY (ma_tin_nhan) REFERENCES TIN_NHAN(ma_tin_nhan);
ALTER TABLE TRICH_DAN ADD CONSTRAINT FK_TRICH_DAN_ma_tai_lieu FOREIGN KEY (ma_tai_lieu) REFERENCES TAI_LIEU(ma_tai_lieu);
ALTER TABLE TAI_LIEU ADD CONSTRAINT FK_TAI_LIEU_ma_tai_khoan FOREIGN KEY (ma_tai_khoan) REFERENCES TAI_KHOAN(ma_tai_khoan);

-- Ghi chú: các ràng buộc không làm được bằng CHECK (giờ hẹn nằm trong buổi làm việc, ngoài giờ nghỉ trưa;
-- một bệnh nhân không có 2 lịch trùng giờ; bước gắn vào lịch phải thuộc lộ trình của chính bệnh nhân) kiểm tra trong code.
