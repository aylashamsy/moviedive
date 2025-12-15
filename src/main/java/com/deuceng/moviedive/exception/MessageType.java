package com.deuceng.moviedive.exception;

import lombok.Getter;

@Getter
public enum MessageType
{INVALID_REQUEST("1002","Geçersiz istek"),
    MISSING_PARAMETER("1003","Eksik parametre"),
    INVALID_PARAMETER("1004","Geçersiz parametre"),
    UNAUTHORIZED("1005","Yetkisiz erişim"),
    FORBIDDEN("1006","Erişim reddedildi"),
    TOKEN_EXPIRED("1007","Oturum süresi doldu"),
    TOKEN_INVALID("1008","Geçersiz token"),
    USER_NOT_FOUND("1009","Kullanıcı bulunamadı"),
    USER_ALREADY_EXISTS("1010","Kullanıcı zaten mevcut"),
    PASSWORD_INCORRECT("1011","Şifre hatalı"),
    PASSWORD_TOO_WEAK("1012","Şifre çok zayıf"),
    EMAIL_ALREADY_USED("1013","E-posta adresi zaten kullanılıyor"),
    EMAIL_NOT_VERIFIED("1014","E-posta doğrulanmamış"),
    ACCOUNT_LOCKED("1015","Hesap kilitlenmiş"),
    ACCOUNT_DISABLED("1016","Hesap pasif"),
    OPERATION_NOT_ALLOWED("1017","İşleme izin verilmiyor"),
    DATA_INTEGRITY_ERROR("1018","Veri bütünlüğü hatası"),
    DATABASE_ERROR("1019","Veritabanı hatası"),
    CONNECTION_ERROR("1020","Bağlantı hatası"),
    TIMEOUT_ERROR("1021","Zaman aşımı oluştu"),
    SERVICE_UNAVAILABLE("1022","Servis kullanılamıyor"),
    EXTERNAL_SERVICE_ERROR("1023","Harici servis hatası"),
    FILE_NOT_FOUND("1024","Dosya bulunamadı"),
    FILE_UPLOAD_FAILED("1025","Dosya yükleme başarısız"),
    FILE_SIZE_EXCEEDED("1026","Dosya boyutu aşıldı"),
    INVALID_FILE_TYPE("1027","Geçersiz dosya tipi"),
    PARSING_ERROR("1028","Veri ayrıştırma hatası"),
    JSON_FORMAT_ERROR("1029","JSON formatı hatalı"),
    XML_FORMAT_ERROR("1030","XML formatı hatalı"),
    DATE_FORMAT_ERROR("1031","Tarih formatı hatalı"),
    NUMBER_FORMAT_ERROR("1032","Sayı formatı hatalı"),
    NULL_POINTER("1033","Boş nesne hatası"),
    ILLEGAL_ARGUMENT("1034","Geçersiz argüman"),
    METHOD_NOT_SUPPORTED("1035","Metot desteklenmiyor"),
    RESOURCE_ALREADY_EXISTS("1036","Kaynak zaten mevcut"),
    RESOURCE_IN_USE("1037","Kaynak kullanımda"),
    LIMIT_EXCEEDED("1038","Limit aşıldı"),
    QUOTA_EXCEEDED("1039","Kota aşıldı"),
    OPERATION_FAILED("1040","İşlem başarısız"),
    OPERATION_CANCELLED("1041","İşlem iptal edildi"),
    VERSION_MISMATCH("1042","Versiyon uyumsuzluğu"),
    CONFIGURATION_ERROR("1043","Konfigürasyon hatası"),
    CACHE_ERROR("1044","Önbellek hatası"),
    UNKNOWN_ERROR("9998","Bilinmeyen hata"),
    GENERAL_EXCEPTION("9999","Genel bir hata oluştu"),
    NO_RECORD_EXIST("1001","Kayıt bulunamdı");


    private String code;
    private String message;

    MessageType(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
