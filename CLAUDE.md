# Hướng dẫn cho Claude khi làm dự án này

Đồ án: website quản lý phòng khám nha khoa + trợ lý AI tra cứu tài liệu nội bộ.

## Đọc trước khi code
- `README.md`: công nghệ, quy tắc code, các bước cần làm (checklist).
- `docs/requirements.md`: quy tắc nghiệp vụ mã BR-xx, API, màn hình.
- `docs/usecase.md`: tác nhân, use case, phân quyền.
- `docs/THIET_KE_CSDL.md` + `database/PhongKhamNhaKhoa_mysql.sql`: CSDL là nguồn chuẩn.

## Khi được bảo "làm giai đoạn N"
1. Đọc mục "Giai đoạn N" trong checklist của `README.md`; bỏ qua các mục đã [x].
2. Tra các mã BR-xx, API, màn hình liên quan trong `docs/requirements.md` và bảng liên quan trong `docs/THIET_KE_CSDL.md`.
3. Trước khi code, nêu ngắn kế hoạch (làm những mục nào, file nào) và các câu hỏi nếu tài liệu chưa rõ.
4. Làm backend trước, frontend sau. Giai đoạn lớn thì làm phần backend, dừng lại cho mình kiểm tra rồi mới làm frontend.
5. Chỉ làm mục [P1] và [P2]; mục [P3] chỉ làm khi được yêu cầu.
6. Không làm sang giai đoạn khác, kể cả khi thấy tiện.

## Luật bắt buộc
- KHÔNG sửa cấu trúc CSDL, không thêm bảng/cột. Entity phải khớp script SQL (`ddl-auto: validate`). Thấy thiết kế có vấn đề thì hỏi, không tự đổi.
- Không dùng Flyway, không Docker.
- Làm đúng phạm vi được giao trong prompt, không làm lan sang giai đoạn khác.
- Nghiệp vụ theo đúng BR-xx; chỗ nào tài liệu không nói rõ thì hỏi lại trước khi đoán.
- Comment tiếng Việt, ngắn, chỉ giải thích lý do/quy tắc (ghi mã BR); không comment điều hiển nhiên.
- Không ghi mật khẩu, API key vào code; dùng `application-local.properties`, `.env.local`.

## Khi xong một phần
- Backend phải build được (`gradlew compileJava`), frontend phải chạy được (`npm run build`).
- Tóm tắt ngắn: đã làm gì, file nào, cách thử (API hoặc màn hình), còn thiếu gì.
- Đánh dấu [x] các mục đã xong trong checklist của `README.md`.
