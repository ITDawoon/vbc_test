package com.rapeech.vbc.rest.dao;

import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rapeech.vbc.data.entity.OMSChannels;
import com.rapeech.vbc.data.repository.OMSChannelsRepository;
import com.rapeech.vbc.ha.config.PropertiesConfig;
import com.rapeech.vbc.rest.model.transfer.EndPoint;
import com.rapeech.vbc.rest.model.transfer.ScaExtra;
import com.rapeech.vbc.rest.model.transfer.ScaRegiInfo;
import com.rapeech.vbc.utils.CommonConverter;
import com.rapeech.vbc.utils.FormatUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


//230427 : HA를 위한 DAO 추가
@Component
@RequiredArgsConstructor
public class HaDao {

	private final OMSChannelsRepository omsChannelsRepository;
	private final PropertiesConfig propertiesConfig;
	FormatUtil formatUtil = new FormatUtil();


	public OMSChannels getChannelInfoById(Long channelId){
		Optional<OMSChannels> omsChannels = omsChannelsRepository.findById(channelId);
		return omsChannels.get();
	}

	public OMSChannels findByChannelNo(long channelNo) {
		return omsChannelsRepository.findByChannelNo(channelNo);
	}

	public List<OMSChannels> getChannelInfo(){
		List<OMSChannels> list = omsChannelsRepository.findAll();
		return list;
	}

	public ScaRegiInfo findByChannelId(String channelId){
		Optional<OMSChannels> omsChannels = omsChannelsRepository.findById(Long.valueOf(channelId));

		Gson gson = new Gson();
		ScaRegiInfo scaRegiInfo = CommonConverter.convertObjectFromTarget(omsChannels.get(), ScaRegiInfo.class);
		if(ObjectUtils.isEmpty(scaRegiInfo.getExtras())) {
			scaRegiInfo.setExtras(gson.fromJson(omsChannels.get().getExtras(), ScaExtra.class));
		}

		if(ObjectUtils.isEmpty(scaRegiInfo.getEndPoints())){
			String endpointStr = omsChannels.get().getEndpoints();
			if(!StringUtils.startsWith(endpointStr, "[")) { // json string is not json array
				StringBuffer sb = new StringBuffer();
				sb.append("[").append(endpointStr).append("]");
				endpointStr = sb.toString();
			}
			scaRegiInfo.setEndPoints(gson.fromJson(endpointStr, new TypeToken<List<EndPoint>>(){}));
			scaRegiInfo.setProfileName(propertiesConfig.getAccessProfileName());
		}

		return scaRegiInfo;
	}

	public ScaRegiInfo parseScaRegiInfo(OMSChannels omsChannels){
		Gson gson = new Gson();
		ScaRegiInfo scaRegiInfo = CommonConverter.convertObjectFromTarget(omsChannels, ScaRegiInfo.class);
		if(ObjectUtils.isEmpty(scaRegiInfo.getExtras())) {
			scaRegiInfo.setExtras(gson.fromJson(omsChannels.getExtras(), ScaExtra.class));
		}

		if(ObjectUtils.isEmpty(scaRegiInfo.getEndPoints())){
			String endpointStr = omsChannels.getEndpoints();
			if(!StringUtils.startsWith(endpointStr, "[")) { // json string is not json array
				StringBuffer sb = new StringBuffer();
				sb.append("[").append(endpointStr).append("]");
				endpointStr = sb.toString();
			}
			scaRegiInfo.setEndPoints(gson.fromJson(endpointStr, new TypeToken<List<EndPoint>>(){}));
			scaRegiInfo.setProfileName(propertiesConfig.getAccessProfileName());
		}

		return scaRegiInfo;
	}


}
