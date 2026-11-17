-- =====================================================================
-- DỮ LIỆU MẪU – Phòng khám nha khoa (MySQL 8) – chạy SAU PhongKhamNhaKhoa_mysql.sql
-- Mật khẩu mọi tài khoản: 123456 (BCrypt, dùng được với Spring Security)
-- Ngày tháng tính theo tuần hiện tại (CURDATE()): có lịch 2 tuần trước, tuần này, tuần sau.
-- =====================================================================
USE phong_kham_nha_khoa;
SET NAMES utf8mb4;

SET @pw = '$2a$10$4HrzrlsxEYMkBFv0yUNPMOGugf9KzRXTlpy0bNeW2l8LcA91BykQq';
SET @today = CURDATE();
SET @mon = DATE_SUB(@today, INTERVAL WEEKDAY(@today) DAY);   -- thứ Hai tuần này

-- ---------------- 1. Tài khoản ----------------
INSERT INTO TAI_KHOAN (ten_dang_nhap, mat_khau, so_dien_thoai, email, vai_tro) VALUES
 ('admin',      @pw, '0905000001', 'admin@nhakhoa.local', 'chu_phong_kham'),
 ('nv.hanh',    @pw, '0905000011', 'hanh@nhakhoa.local',  'nhan_vien'),
 ('nv.tuan',    @pw, '0905000012', 'tuan@nhakhoa.local',  'nhan_vien'),
 ('bs.an',      @pw, '0905000021', 'an@nhakhoa.local',    'bac_si'),
 ('bs.binh',    @pw, '0905000022', 'binh@nhakhoa.local',  'bac_si'),
 ('bs.chau',    @pw, '0905000023', 'chau@nhakhoa.local',  'bac_si'),
 ('bs.dung',    @pw, '0905000024', 'dung@nhakhoa.local',  'bac_si'),
 ('lan.nguyen', @pw, '0912345678', 'lan@example.com',     'benh_nhan'),
 ('minh.tran',  @pw, '0987654321', NULL,                  'benh_nhan'),
 ('hoa.le',     @pw, '0934567890', 'hoa@example.com',     'benh_nhan');

-- ---------------- 2. Nhân viên, bác sĩ ----------------
INSERT INTO NHAN_VIEN (ma_tai_khoan, ho_ten, ngay_vao_lam)
SELECT ma_tai_khoan, 'Phạm Thị Hạnh', '2024-03-01' FROM TAI_KHOAN WHERE ten_dang_nhap = 'nv.hanh'
UNION ALL
SELECT ma_tai_khoan, 'Lê Minh Tuấn', '2025-01-15' FROM TAI_KHOAN WHERE ten_dang_nhap = 'nv.tuan';

INSERT INTO BAC_SI (ma_tai_khoan, ho_ten, chuyen_mon, bang_cap, gioi_thieu)
SELECT t.ma_tai_khoan, v.ho_ten, v.chuyen_mon, v.bang_cap, v.gioi_thieu
FROM (
  SELECT 'bs.an' tk, 'BS. Nguyễn Văn An' ho_ten, 'Phục hình răng sứ' chuyen_mon, 'Bác sĩ Răng Hàm Mặt – ĐH Y Dược' bang_cap, '10 năm kinh nghiệm phục hình, bọc sứ thẩm mỹ.' gioi_thieu
  UNION ALL SELECT 'bs.binh', 'BS. Trần Thị Bình', 'Chỉnh nha', 'Thạc sĩ Răng Hàm Mặt', 'Chuyên niềng răng mắc cài và khay trong suốt.'
  UNION ALL SELECT 'bs.chau', 'BS. Lê Hoàng Châu', 'Nội nha, nha tổng quát', 'Bác sĩ Răng Hàm Mặt', 'Điều trị tủy, trám răng, nhổ răng khôn.'
  UNION ALL SELECT 'bs.dung', 'BS. Võ Thị Dung', 'Nha khoa trẻ em', 'Bác sĩ Răng Hàm Mặt', 'Khám và điều trị răng cho trẻ em.'
) v JOIN TAI_KHOAN t ON t.ten_dang_nhap = v.tk;

SELECT n.ma_nhan_vien INTO @nv1 FROM NHAN_VIEN n JOIN TAI_KHOAN t USING (ma_tai_khoan) WHERE t.ten_dang_nhap = 'nv.hanh';
SELECT b.ma_bac_si INTO @bsAn   FROM BAC_SI b JOIN TAI_KHOAN t USING (ma_tai_khoan) WHERE t.ten_dang_nhap = 'bs.an';
SELECT b.ma_bac_si INTO @bsBinh FROM BAC_SI b JOIN TAI_KHOAN t USING (ma_tai_khoan) WHERE t.ten_dang_nhap = 'bs.binh';
SELECT b.ma_bac_si INTO @bsChau FROM BAC_SI b JOIN TAI_KHOAN t USING (ma_tai_khoan) WHERE t.ten_dang_nhap = 'bs.chau';
SELECT b.ma_bac_si INTO @bsDung FROM BAC_SI b JOIN TAI_KHOAN t USING (ma_tai_khoan) WHERE t.ten_dang_nhap = 'bs.dung';

-- ---------------- 3. Dịch vụ ----------------
INSERT INTO DICH_VU (ten_dich_vu, mo_ta, gia, thoi_luong) VALUES
 ('Khám và tư vấn',       'Khám tổng quát, tư vấn hướng điều trị',      100000,   30),
 ('Chụp X-quang răng',    'Chụp phim quanh chóp hoặc toàn hàm',          150000,   15),
 ('Lấy cao răng',         'Cạo vôi răng, đánh bóng',                    300000,   30),
 ('Trám răng composite',  'Trám răng sâu bằng vật liệu composite',      400000,   30),
 ('Trám răng sữa trẻ em', 'Trám răng sữa cho trẻ',                      250000,   30),
 ('Nhổ răng thường',      'Nhổ răng không phẫu thuật',                  500000,   30),
 ('Nhổ răng khôn',        'Nhổ răng khôn có tiểu phẫu',                2000000,   60),
 ('Điều trị tủy',         'Lấy tủy, làm sạch và trám bít ống tủy',     1500000,   60),
 ('Điều trị viêm nướu',   'Làm sạch túi nướu, hướng dẫn vệ sinh',       600000,   30),
 ('Mài răng, lấy dấu',    'Mài cùi răng và lấy dấu làm răng sứ',        500000,   30),
 ('Bọc răng sứ',          'Gắn mão sứ (giá tính cho 1 răng)',          3500000,   60),
 ('Tẩy trắng răng',       'Tẩy trắng tại phòng khám',                  2500000,   60),
 ('Niềng răng mắc cài',   'Trọn gói niềng răng mắc cài kim loại',     30000000,   60),
 ('Cấy ghép Implant',     'Cấy trụ Implant (chưa gồm mão sứ)',        18000000,   90),
 ('Tái khám, kiểm tra',   'Kiểm tra sau điều trị',                           0,   15);

SELECT ma_dich_vu INTO @dvKham  FROM DICH_VU WHERE ten_dich_vu = 'Khám và tư vấn';
SELECT ma_dich_vu INTO @dvCao   FROM DICH_VU WHERE ten_dich_vu = 'Lấy cao răng';
SELECT ma_dich_vu INTO @dvTram  FROM DICH_VU WHERE ten_dich_vu = 'Trám răng composite';
SELECT ma_dich_vu INTO @dvTuy   FROM DICH_VU WHERE ten_dich_vu = 'Điều trị tủy';
SELECT ma_dich_vu INTO @dvMai   FROM DICH_VU WHERE ten_dich_vu = 'Mài răng, lấy dấu';
SELECT ma_dich_vu INTO @dvSu    FROM DICH_VU WHERE ten_dich_vu = 'Bọc răng sứ';
SELECT ma_dich_vu INTO @dvTrang FROM DICH_VU WHERE ten_dich_vu = 'Tẩy trắng răng';

-- ---------------- 4. Lịch làm việc: 2 tuần trước, tuần này, tuần sau (thứ 2 – thứ 7) ----------------
-- thu: 0 = thứ Hai ... 5 = thứ Bảy
INSERT INTO LICH_LAM_VIEC (ma_bac_si, ngay, gio_bat_dau, gio_ket_thuc)
SELECT m.bs, DATE_ADD(@mon, INTERVAL (w.tuan * 7 + m.thu) DAY), m.bd, m.kt
FROM (SELECT -2 tuan UNION ALL SELECT -1 UNION ALL SELECT 0 UNION ALL SELECT 1) w
CROSS JOIN (
            SELECT @bsAn bs, 0 thu, '08:00' bd, '17:00' kt
  UNION ALL SELECT @bsAn, 1, '08:00', '17:00'   UNION ALL SELECT @bsAn, 2, '08:00', '17:00'
  UNION ALL SELECT @bsAn, 3, '08:00', '17:00'   UNION ALL SELECT @bsAn, 4, '08:00', '17:00'
  UNION ALL SELECT @bsAn, 5, '08:00', '11:30'
  UNION ALL SELECT @bsBinh, 0, '08:00', '17:00' UNION ALL SELECT @bsBinh, 1, '13:30', '17:00'
  UNION ALL SELECT @bsBinh, 2, '08:00', '17:00' UNION ALL SELECT @bsBinh, 3, '13:30', '17:00'
  UNION ALL SELECT @bsBinh, 4, '08:00', '17:00'
  UNION ALL SELECT @bsChau, 1, '08:00', '17:00' UNION ALL SELECT @bsChau, 2, '08:00', '17:00'
  UNION ALL SELECT @bsChau, 3, '08:00', '17:00' UNION ALL SELECT @bsChau, 4, '08:00', '17:00'
  UNION ALL SELECT @bsChau, 5, '08:00', '17:00'
  UNION ALL SELECT @bsDung, 0, '08:00', '11:30' UNION ALL SELECT @bsDung, 1, '08:00', '11:30'
  UNION ALL SELECT @bsDung, 2, '08:00', '11:30' UNION ALL SELECT @bsDung, 3, '08:00', '11:30'
  UNION ALL SELECT @bsDung, 4, '08:00', '11:30'
) m;

-- BS Bình nghỉ thứ Tư tuần trước
SET @ngayBinhNghi = DATE_SUB(@mon, INTERVAL 5 DAY);
UPDATE LICH_LAM_VIEC SET trang_thai = 'nghi', ghi_chu = 'Nghỉ ốm'
WHERE ma_bac_si = @bsBinh AND ngay = @ngayBinhNghi;

-- ---------------- 5. Bệnh nhân (hồ sơ sinh ra khi đặt lịch) và tiền sử ----------------
INSERT INTO BENH_NHAN (ma_tai_khoan, ho_ten, ngay_sinh, gioi_tinh, dia_chi)
SELECT t.ma_tai_khoan, v.ho_ten, v.ngay_sinh, v.gioi_tinh, v.dia_chi
FROM (
            SELECT 'lan.nguyen' tk, 'Nguyễn Thị Lan' ho_ten, '1990-05-12' ngay_sinh, 'Nữ' gioi_tinh, '12 Lê Duẩn, Hải Châu, Đà Nẵng' dia_chi
  UNION ALL SELECT 'lan.nguyen', 'Trần Bảo Na',   '2018-09-03', 'Nữ',  '12 Lê Duẩn, Hải Châu, Đà Nẵng'
  UNION ALL SELECT 'minh.tran',  'Trần Văn Minh', '1985-11-20', 'Nam', '45 Nguyễn Văn Linh, Thanh Khê, Đà Nẵng'
  UNION ALL SELECT 'hoa.le',     'Lê Thị Hoa',    '1962-02-14', 'Nữ',  '8 Hùng Vương, Hải Châu, Đà Nẵng'
) v JOIN TAI_KHOAN t ON t.ten_dang_nhap = v.tk;

SELECT ma_benh_nhan INTO @bnLan  FROM BENH_NHAN WHERE ho_ten = 'Nguyễn Thị Lan';
SELECT ma_benh_nhan INTO @bnNa   FROM BENH_NHAN WHERE ho_ten = 'Trần Bảo Na';
SELECT ma_benh_nhan INTO @bnMinh FROM BENH_NHAN WHERE ho_ten = 'Trần Văn Minh';
SELECT ma_benh_nhan INTO @bnHoa  FROM BENH_NHAN WHERE ho_ten = 'Lê Thị Hoa';

INSERT INTO TIEN_SU_BENH (ma_benh_nhan, loai_tien_su, ten_tien_su, ghi_chu) VALUES
 (@bnLan,  'di_ung',          'Penicillin',        'Nổi mẩn đỏ khi dùng Amoxicillin'),
 (@bnMinh, 'benh_nen',        'Tiểu đường type 2', 'Phát hiện 2020, đang điều trị'),
 (@bnMinh, 'thuoc_dang_dung', 'Metformin 500mg',   'Uống 2 lần/ngày'),
 (@bnHoa,  'benh_nen',        'Tăng huyết áp',     NULL),
 (@bnHoa,  'thuoc_dang_dung', 'Aspirin 81mg',      'Thuốc chống kết tập tiểu cầu, lưu ý khi nhổ răng'),
 (@bnHoa,  'can_thiep_rang',  'Nhổ răng 36',       'Năm 2023');

-- ---------------- 6. Lượt khám đã hoàn thành: chị Lan, thứ Hai tuần trước 09:00 với BS An ----------------
SET @ngayKham = DATE_SUB(@mon, INTERVAL 7 DAY);
SELECT ma_lich INTO @lichAnTuanTruoc FROM LICH_LAM_VIEC WHERE ma_bac_si = @bsAn AND ngay = @ngayKham;

INSERT INTO LICH_HEN (ma_benh_nhan, ma_lich, gio_hen, ly_do_kham, loai_lich_hen, trang_thai, ngay_tao)
VALUES (@bnLan, @lichAnTuanTruoc, '09:00', 'Muốn bọc sứ răng cửa bị xỉn màu, răng hàm trên bên phải hay ê buốt',
        'kham_moi', 'hoan_thanh', TIMESTAMP(DATE_SUB(@ngayKham, INTERVAL 3 DAY), '20:15:00'));
SET @lhLan1 = LAST_INSERT_ID();

INSERT INTO HO_SO_KHAM (ma_lich_hen, ma_nhan_vien, ngay_kham, so_thu_tu, trieu_chung, chan_doan, noi_dung_dieu_tri, ghi_chu)
VALUES (@lhLan1, @nv1, TIMESTAMP(@ngayKham, '08:55:00'), 1,
        'Ê buốt răng 16 khi uống lạnh; răng 11 đổi màu, đau âm ỉ về đêm',
        'Sâu răng 16 (sâu ngà); viêm tủy không hồi phục răng 11',
        'Lấy cao răng; trám composite răng 16; điều trị tủy răng 11 (lần 1)',
        'Dị ứng Penicillin – không kê nhóm Penicillin. Hẹn mài răng, lấy dấu răng 11 sau 2 tuần.');
SET @hskLan1 = LAST_INSERT_ID();

INSERT INTO SO_DO_RANG (ma_ho_so_kham, so_rang, tinh_trang, ghi_chu) VALUES
 (@hskLan1, 11, 'sau_rang',    'Viêm tủy, đã điều trị tủy lần 1, chờ bọc sứ'),
 (@hskLan1, 16, 'da_tram',     'Trám composite mặt nhai'),
 (@hskLan1, 21, 'binh_thuong', NULL),
 (@hskLan1, 36, 'binh_thuong', NULL);

-- Lộ trình bọc sứ răng 11 – 3 bước, bước 1 làm ngay trong lần khám này
INSERT INTO LO_TRINH_DIEU_TRI (ma_benh_nhan, ma_bac_si, ten_lo_trinh, ngay_bat_dau)
VALUES (@bnLan, @bsAn, 'Bọc sứ răng 11', @ngayKham);
SET @ltLan = LAST_INSERT_ID();

INSERT INTO BUOC_DIEU_TRI (ma_lo_trinh, ma_dich_vu, thu_tu, noi_dung, ngay_du_kien, trang_thai) VALUES
 (@ltLan, @dvTuy, 1, 'Điều trị tủy răng 11',                  @ngayKham,                           'da_thuc_hien'),
 (@ltLan, @dvMai, 2, 'Mài răng 11, lấy dấu làm mão sứ',        DATE_ADD(@today, INTERVAL 2 DAY),    'chua_thuc_hien'),
 (@ltLan, @dvSu,  3, 'Gắn mão sứ răng 11, kiểm tra khớp cắn',  DATE_ADD(@today, INTERVAL 16 DAY),   'chua_thuc_hien');
SELECT ma_buoc INTO @buoc1 FROM BUOC_DIEU_TRI WHERE ma_lo_trinh = @ltLan AND thu_tu = 1;
SELECT ma_buoc INTO @buoc2 FROM BUOC_DIEU_TRI WHERE ma_lo_trinh = @ltLan AND thu_tu = 2;

-- Dịch vụ của lượt khám: bệnh nhân đăng ký bọc sứ (bác sĩ hoãn), bác sĩ làm 3 dịch vụ
INSERT INTO CHI_TIET_LICH_HEN (ma_lich_hen, ma_dich_vu, ma_buoc, so_rang, trang_thai, ghi_chu) VALUES
 (@lhLan1, @dvSu,   NULL,   11,   'khong_thuc_hien', 'Cần điều trị tủy trước, đưa vào lộ trình'),
 (@lhLan1, @dvCao,  NULL,   NULL, 'da_thuc_hien',    NULL),
 (@lhLan1, @dvTram, NULL,   16,   'da_thuc_hien',    NULL),
 (@lhLan1, @dvTuy,  @buoc1, 11,   'da_thuc_hien',    'Bước 1 của lộ trình');

-- Nhắc tái khám bước 2 (đến hạn trong 2 ngày)
INSERT INTO THONG_BAO (ma_buoc, loai_thong_bao, noi_dung)
VALUES (@buoc2, 'nhac_tai_kham', 'Sắp đến lịch "Mài răng 11, lấy dấu làm mão sứ" của Nguyễn Thị Lan. Vui lòng đặt lịch tái khám.');

-- ---------------- 7. Lịch bị hủy do bác sĩ nghỉ: cô Hoa, thứ Tư tuần trước với BS Bình ----------------
SELECT ma_lich INTO @lichBinhNghi FROM LICH_LAM_VIEC WHERE ma_bac_si = @bsBinh AND ngay = @ngayBinhNghi;
INSERT INTO LICH_HEN (ma_benh_nhan, ma_lich, gio_hen, ly_do_kham, trang_thai, ghi_chu)
VALUES (@bnHoa, @lichBinhNghi, '14:00', 'Răng hàm dưới lung lay', 'da_huy', 'Hủy do bác sĩ nghỉ ốm');
SET @lhHoaHuy = LAST_INSERT_ID();
INSERT INTO THONG_BAO (ma_lich_hen, loai_thong_bao, noi_dung, da_doc)
VALUES (@lhHoaHuy, 'huy_lich', 'Lịch khám 14:00 với BS. Trần Thị Bình đã bị hủy do bác sĩ nghỉ. Vui lòng đặt lại lịch khác.', 1);

-- ---------------- 8. Lịch sắp tới (thứ Hai tuần sau) ----------------
SET @thuHaiTuanSau = DATE_ADD(@mon, INTERVAL 7 DAY);
SELECT ma_lich INTO @lichAnTuanSau   FROM LICH_LAM_VIEC WHERE ma_bac_si = @bsAn   AND ngay = @thuHaiTuanSau;
SELECT ma_lich INTO @lichDungTuanSau FROM LICH_LAM_VIEC WHERE ma_bac_si = @bsDung AND ngay = @thuHaiTuanSau;

INSERT INTO LICH_HEN (ma_benh_nhan, ma_lich, gio_hen, ly_do_kham)
VALUES (@bnMinh, @lichAnTuanSau, '09:00', 'Răng hàm dưới bên trái đau khi nhai, muốn tẩy trắng răng');
INSERT INTO CHI_TIET_LICH_HEN (ma_lich_hen, ma_dich_vu) VALUES (LAST_INSERT_ID(), @dvTrang);

INSERT INTO LICH_HEN (ma_benh_nhan, ma_lich, gio_hen, ly_do_kham)
VALUES (@bnNa, @lichDungTuanSau, '08:30', 'Bé kêu đau răng, có lỗ đen ở răng hàm');
INSERT INTO CHI_TIET_LICH_HEN (ma_lich_hen, ma_dich_vu) VALUES (LAST_INSERT_ID(), @dvKham);

SELECT CONCAT('Đã nạp dữ liệu mẫu. Tuần hiện tại bắt đầu: ', @mon) AS ket_qua;
