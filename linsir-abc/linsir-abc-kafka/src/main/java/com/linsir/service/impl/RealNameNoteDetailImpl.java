package com.linsir.service.impl;

import com.linsir.dao.RealNameNoteDetailDao;
import com.linsir.entity.RealNameNoteDetail;
import com.linsir.service.RealNameNoteDetailService;

import java.sql.SQLException;
import java.util.List;

public class RealNameNoteDetailImpl implements RealNameNoteDetailService {

    private final RealNameNoteDetailDao realNameNoteDetailDao;

    //private final KafkaService kafkaService;



    public  RealNameNoteDetailImpl() throws SQLException, ClassNotFoundException {
        realNameNoteDetailDao = new RealNameNoteDetailDao();
        //kafkaService = new KafkaServiceImpl();
    }

    public List<RealNameNoteDetail> getRealNameNoteDetails() throws SQLException {

        String sql = "select * from real_name_note_detail limit 1,100";
        return realNameNoteDetailDao.getAll(sql);
    }




}
