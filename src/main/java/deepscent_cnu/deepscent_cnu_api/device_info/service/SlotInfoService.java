package deepscent_cnu.deepscent_cnu_api.device_info.service;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.request.RegisterSlotInfoRequest;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.request.TargetMemberIdRequest;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.response.CapsuleInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.response.CapsuleInfoResponse;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.SlotInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.repository.SlotInfoRepository;
import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.exception.MemberException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SlotInfoService {

  private final SlotInfoRepository slotInfoRepository;
  private final MemberRepository memberRepository;

  public SlotInfoService(SlotInfoRepository slotInfoRepository, MemberRepository memberRepository) {
    this.slotInfoRepository = slotInfoRepository;
    this.memberRepository = memberRepository;
  }

  public void registerSlotInfo(RegisterSlotInfoRequest request) {
    Member targetMember = memberRepository.findById(request.targetMemberId())
        .orElseThrow(() -> new MemberException(
            ErrorCode.TARGET_MEMBER_NOT_FOUND));

    slotInfoRepository.save(
        new SlotInfo(targetMember, request.deviceNumber(), request.fanNumber(), request.scent()));
  }

  public CapsuleInfoResponse getSlotInfoList(TargetMemberIdRequest request) {
    List<CapsuleInfo> capsuleInfoList = new ArrayList<>(Collections.nCopies(12, null));
    Member targetMember = memberRepository.findById(request.targetMemberId())
        .orElseThrow(() -> new MemberException(
            ErrorCode.TARGET_MEMBER_NOT_FOUND));
    List<SlotInfo> slotInfoList = slotInfoRepository.findAllByMember(targetMember);

    for (SlotInfo slotInfo : slotInfoList) {
      Integer deviceNumber = slotInfo.getDeviceNumber();
      Integer fanNumber = slotInfo.getFanNumber();
      String scent = slotInfo.getScent();
      int slotInfoListIdx = (deviceNumber - 1) * 4 + (fanNumber - 1);

      capsuleInfoList.set(slotInfoListIdx, new CapsuleInfo(scent, deviceNumber, fanNumber));
    }

    return new CapsuleInfoResponse(capsuleInfoList);
  }
}
