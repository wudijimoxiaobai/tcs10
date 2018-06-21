package com.csscaps.tcs;

import com.alibaba.fastjson.JSON;
import com.csscaps.tcs.database.table.Invoice;
import com.csscaps.tcs.database.table.Invoice_Table;
import com.csscaps.tcs.database.table.ProductModel;
import com.csscaps.tcs.database.table.ProductModel_Table;
import com.csscaps.tcs.model.RequestInvoiceResult;
import com.csscaps.tcs.model.RequestUploadInvoice;
import com.tax.fcr.library.network.Api;
import com.tax.fcr.library.network.IPresenter;
import com.tax.fcr.library.network.RequestModel;

import java.util.List;
import java.util.TimerTask;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;

/**
 * Created by tl on 2018/6/20.
 */

public class MyTimerTask extends TimerTask implements IPresenter {

    private final String DISA = "DISA";
    private final String NEG = "NEG";

    @Override
    public void run() {
        List<Invoice> disas = select().from(Invoice.class).where(Invoice_Table.requestType.eq(DISA)).and(Invoice_Table.approveFlag.eq("4")).and(Invoice_Table.requestStatus.eq("1")).queryList();
        List<Invoice> engs = select().from(Invoice.class).where(Invoice_Table.requestType.eq(NEG)).and(Invoice_Table.approveFlag.eq("4")).and(Invoice_Table.requestStatus.eq("1")).queryList();
        List<Invoice> disas1 = select().from(Invoice.class).where(Invoice_Table.requestType.eq(DISA)).and(Invoice_Table.approveFlag.eq("4")).and(Invoice_Table.requestStatus.eq("0")).queryList();
        List<Invoice> engs1 = select().from(Invoice.class).where(Invoice_Table.requestType.eq(NEG)).and(Invoice_Table.approveFlag.eq("4")).and(Invoice_Table.requestStatus.eq("0")).queryList();
        requestResult(disas, ServerConstants.ATCS019);
//        requestResult(engs, ServerConstants.ATCS017);
//        request(disas1, ServerConstants.ATCS018);
//        request(engs1, ServerConstants.ATCS016);
    }

    private void requestResult(List<Invoice> list, String funId) {
        RequestInvoiceResult requestInvoiceResult = new RequestInvoiceResult();
        requestInvoiceResult.setFuncid(funId);
        requestInvoiceResult.setInvoice_data(list);
        RequestModel requestModel = new RequestModel();
        requestModel.setFuncid(requestInvoiceResult.getFuncid());
        requestInvoiceResult.setDevicesn(requestModel.getDevicesn());
        requestModel.setData(JSON.toJSONString(requestInvoiceResult));
        Api.post(this, requestModel);
    }

    private void request(List<Invoice> list, String funId) {
        RequestUploadInvoice uploadInvoice = new RequestUploadInvoice();
        for (Invoice invoice : list) {
            String invoiceNo = invoice.getInvoice_no();
            List<ProductModel> productModels = select().from(ProductModel.class).where(ProductModel_Table.invoice_no.eq(invoiceNo)).queryList();
            invoice.setGoods(productModels);
            uploadInvoice.getInvoice_data().add(invoice);
        }
        uploadInvoice.setFuncid(funId);
        RequestModel requestModel = new RequestModel();
        requestModel.setFuncid(uploadInvoice.getFuncid());
        uploadInvoice.setDevicesn(requestModel.getDevicesn());
        requestModel.setData(JSON.toJSONString(uploadInvoice));
        Api.post(this, requestModel);
    }

    @Override
    public void onSuccess(String requestPath, String objectString) {

        switch (requestPath) {
            case ServerConstants.ATCS016://负数发票申请

                break;
            case ServerConstants.ATCS017://负数发票申请结果

                break;
            case ServerConstants.ATCS018://作废发票申请

                break;
            case ServerConstants.ATCS019://作废发票申请结果

                break;
        }
    }

    @Override
    public void onFailure(String requestPath, String errorMes) {

    }


}