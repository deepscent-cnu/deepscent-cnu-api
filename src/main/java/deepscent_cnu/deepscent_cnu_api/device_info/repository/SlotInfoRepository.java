package deepscent_cnu.deepscent_cnu_api.device_info.repository;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.SlotInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotInfoRepository extends JpaRepository<SlotInfo, Long> {

  List<SlotInfo> findAllByMember(Member member);
}
