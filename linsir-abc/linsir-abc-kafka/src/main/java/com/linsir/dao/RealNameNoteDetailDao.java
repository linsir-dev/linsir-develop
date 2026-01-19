package com.linsir.dao;

import com.linsir.entity.RealNameNoteDetail;

public class RealNameNoteDetailDao extends BaseDao<RealNameNoteDetail> {
//    public List<RealNameNoteDetail> getAll(String sql) {
//        List<RealNameNoteDetail> realNameNoteDetails = new ArrayList<RealNameNoteDetail>();
//        ResultSet resultSet = connection.createStatement().executeQuery(sql);
//        while (resultSet.next()) {
//            RealNameNoteDetail realNameNoteDetail = new RealNameNoteDetail();
//            realNameNoteDetail.setId(resultSet.getInt("record_id"));
//            realNameNoteDetail.setRecordId(resultSet.getString("record_id"));
//            realNameNoteDetail.setNoteType(resultSet.getString("note_type"));
//            realNameNoteDetail.setNote(resultSet.getString("note"));
//            realNameNoteDetail.setNoteId(resultSet.getString("note_id"));
//            realNameNoteDetail.setOrderNumber(resultSet.getString("order_number"));
//            realNameNoteDetail.setAuthTime(resultSet.getString("auth_time"));
//            realNameNoteDetail.setEndTime(resultSet.getString("end_time"));
//            realNameNoteDetail.setUser(resultSet.getString("user"));
//            realNameNoteDetail.setIpAddress(resultSet.getString("ip_address"));
//            realNameNoteDetail.setIdCard(resultSet.getString("id_card"));
//            realNameNoteDetail.setName(resultSet.getString("name"));
//            realNameNoteDetail.setFrontUrl(resultSet.getString("front_url"));
//            realNameNoteDetail.setReverseUrl(resultSet.getString("reverse_url"));
//            realNameNoteDetail.setSelfPhone(resultSet.getString("self_phone"));
//            realNameNoteDetail.setKinsfolkPhone(resultSet.getString("kinsfolk_phone"));
//            realNameNoteDetail.setCityAppId(resultSet.getString("city_app_id"));
//            realNameNoteDetail.setCityId(resultSet.getString("city_id"));
//            realNameNoteDetail.setCityName(resultSet.getString("city_name"));
//            realNameNoteDetail.setAppId(resultSet.getString("app_id"));
//            realNameNoteDetail.setAppName(resultSet.getString("app_name"));
//            realNameNoteDetail.setDelFlag(resultSet.getBoolean("del_flag"));
//            realNameNoteDetail.setGmtCreate(resultSet.getString("gmt_create"));
//            realNameNoteDetail.setGmtModified(resultSet.getString("gmt_modified"));
//            realNameNoteDetails.add(realNameNoteDetail);
//        }
//        connection.close();
//        return realNameNoteDetails;
//    }

}
