package com.beliautopart.beliautopart.app;

/**
 * Created by brandon on 12/05/16.
 */
public class AppConfig {
    public static String SERVER = "http://api.beliautopart.com/";
    public static String URL_PARTS_GET = SERVER + "parts.php?action=get";
    public static String URL_PARTS_SEARCH = SERVER + "parts.php?action=getSearchMenu";
    public static String URL_PARTS_TIPE = SERVER + "parts.php?action=getTipeSearch";
    public static String URL_USER_REGISTER = SERVER + "user.php?action=register";
    public static String URL_USER_LOGIN = SERVER + "user.php?action=login";
    public static String URL_USER_UPDATE = SERVER + "user.php?action=updateUser";
    public static String URL_USER_GET_PASSWORD = SERVER + "user.php?action=getPaswword";
    public static String URL_USER_PROFILE = SERVER + "user.php?action=getProfile";
    public static String URL_USER_KAB = SERVER + "user.php?action=getKab";
    public static String URL_ORDER = SERVER + "order.php?action=newOrder";
    public static String URL_ORDERS_STATUS = SERVER + "order.php?action=getStatus";
    public static String URL_ORDERS_CEK_AKTIF = SERVER + "order.php?action=getOrderAktif";
    public static String URL_ORDERS_DETAIL = SERVER + "order.php?action=getDetail";
    public static String URL_ORDERS_HISTORY_DETAIL = SERVER + "order.php?action=getHistoryDetail";
    public static String URL_ORDERS_HISTORY_JOB_DETAIL = SERVER + "order.php?action=getHistoryJobDetail" ;
    public static String URL_ORDERS_PEMBAYARAN = SERVER + "order.php?action=setPembayaran";
    public static String URL_ORDERS_WAKTU_PEMBAYARAN = SERVER + "order.php?action=getTimePembayaran";
    public static String URL_ORDERS_REKENING = SERVER + "order.php?action=getRekening";
    public static String URL_ORDERS_BANK = SERVER + "order.php?action=getBank";
    public static String URL_ORDERS_KONFIRMASI = SERVER + "order.php?action=setKonfirmasiPembayaran";
    public static String URL_ORDERS_VERIFIKASI = SERVER + "order.php?action=getDetailVerifikasi";
    public static String URL_ORDERS_BARANG_DIANTAR = SERVER + "order.php?action=getDetailBarangDiantar";
    public static String URL_ORDERS_BARANG_DETAIL_KURIR = SERVER + "order.php?action=getDetailOrderKurir";
    public static String URL_ORDERS_BARANG_DITERIMA = SERVER + "order.php?action=setBarangDiterima";
    public static String URL_ORDERS_BARANG_DIREJECT = SERVER + "order.php?action=setBarangDireject";
    public static String URL_ORDERS_LIST = SERVER + "order.php?action=getListOrder";
    public static String URL_ORDERS_BATAL = SERVER + "order.php?action=setBatal";
    public static String URL_ORDERS_KOMPLAIN = SERVER + "order.php?action=setKomplain";
    public static String URL_ORDERS_KOMPLAIN_LIST = SERVER + "order.php?action=getKomplain";
    public static String URL_ORDERS_KOMPLAIN_DETAIL = SERVER + "order.php?action=getKomplainDetail";
    public static String URL_ORDERS_KURIR_LOCATION = SERVER + "order.php?action=getKurirLocation" ;
    public static String URL_BENGKEL_LOCATION = SERVER + "bengkel.php?action=getBengkelLocation";
    public static String URL_BENGKEL_JOB = SERVER + "bengkel.php?action=setJobBengkel";
    public static String URL_BENGKEL_JOB_READY_BAYAR = SERVER + "bengkel.php?action=setReadyBayar";
    public static String URL_BENGKEL_JOB_DETAIL = SERVER + "bengkel.php?action=cekJobBengkel";
    public static String URL_BENGKEL_JOB_DETAIL_USER = SERVER + "bengkel.php?action=getJobBengkelDetail";
    public static String URL_BENGKEL_JOB_DONE = SERVER + "bengkel.php?action=setJobBengkelDone";
    public static String URL_BENGKEL_SEND_CHAT = SERVER + "bengkel.php?action=sendMessage";
    public static String URL_BENGKEL_SEND_CHAT_IMAGE = SERVER + "bengkel.php?action=sendImageMessage";
    public static String URL_BENGKEL_READ_CHAT = SERVER + "bengkel.php?action=getMessage";
    public static String URL_BENGKEL_SET_PEMBAYARAN = SERVER + "bengkel.php?action=setPembayaranJob";
    public static String URL_BENGKEL_SET_PEMBAYARAN_BIAYA = SERVER + "bengkel.php?action=setPembayaranBiaya";
    public static String URL_BENGKEL_BATAL = SERVER + "bengkel.php?action=setPembatalanJob";
    public static String URL_HIBURAN_KATEGORI = SERVER + "hiburan.php?action=getHiburan";
    public static String URL_HIBURAN_FOLDER = SERVER + "hiburan.php?action=cekFolder";
    public static String URL_HIBURAN_KONTENT = SERVER + "hiburan.php?action=getHiburanKonten";
    public static String URL_HIBURAN_DETAIL = SERVER + "hiburan.php?action=getHiburanDetail";
    public static String URL_WEBKONTENT_ABOUT = SERVER + "webcontent.php?action=aboutus";
    public static String URL_WEBKONTENT_SETTING = SERVER + "webcontent.php?action=load_setting";
    public static String URL_BENGKEL_JOB_DATANG = SERVER +"bengkel.php?action=setBengkelDatang";
}
