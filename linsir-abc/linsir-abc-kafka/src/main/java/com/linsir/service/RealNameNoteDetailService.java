package com.linsir.service;

import com.linsir.entity.RealNameNoteDetail;

import java.sql.SQLException;
import java.util.List;

public interface RealNameNoteDetailService {

    List<RealNameNoteDetail> getRealNameNoteDetails() throws SQLException;


}
