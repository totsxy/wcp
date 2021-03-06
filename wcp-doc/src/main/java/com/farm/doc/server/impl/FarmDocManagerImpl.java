package com.farm.doc.server.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farm.authority.domain.User;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.doc.dao.FarmDocDaoInter;
import com.farm.doc.dao.FarmDocenjoyDaoInter;
import com.farm.doc.dao.FarmDocfileDaoInter;
import com.farm.doc.dao.FarmDocruninfoDaoInter;
import com.farm.doc.dao.FarmDocruninfoDetailDaoInter;
import com.farm.doc.dao.FarmDoctextDaoInter;
import com.farm.doc.dao.FarmDoctypeDaoInter;
import com.farm.doc.dao.FarmRfDoctextfileDaoInter;
import com.farm.doc.dao.FarmRfDoctypeDaoInter;
import com.farm.doc.dao.FarmtopDaoInter;
import com.farm.doc.domain.Doc;
import com.farm.doc.domain.FarmDocfile;
import com.farm.doc.domain.FarmDocgroup;
import com.farm.doc.domain.FarmDocruninfo;
import com.farm.doc.domain.FarmDoctext;
import com.farm.doc.domain.FarmDoctype;
import com.farm.doc.domain.FarmRfDoctextfile;
import com.farm.doc.domain.FarmRfDoctype;
import com.farm.doc.domain.ex.DocEntire;
import com.farm.doc.domain.ex.TypeBrief;
import com.farm.doc.exception.CanNoDeleteException;
import com.farm.doc.exception.CanNoReadException;
import com.farm.doc.exception.CanNoWriteException;
import com.farm.doc.exception.DocNoExistException;
import com.farm.doc.server.FarmDocIndexInter;
import com.farm.doc.server.FarmDocManagerInter;
import com.farm.doc.server.FarmDocOperateRightInter;
import com.farm.doc.server.FarmDocOperateRightInter.POP_TYPE;
import com.farm.doc.server.FarmDocRunInfoInter;
import com.farm.doc.server.FarmDocTypeInter;
import com.farm.doc.server.FarmDocgroupManagerInter;
import com.farm.doc.server.FarmFileIndexManagerInter;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.UsermessageServiceInter;
import com.farm.doc.server.commons.DocumentConfig;
import com.farm.doc.server.commons.FarmDocFiles;
import com.farm.doc.server.plus.FarmTypePopServerInter;
import com.farm.doc.server.plus.domain.DocAudit;
import com.farm.doc.server.plus.domain.Doctypepop;
import com.farm.doc.util.DocTagUtils;
import com.farm.doc.util.HtmlUtils;

/**
 * ????????????
 * 
 * @author MAC_wd
 */
@Service
public class FarmDocManagerImpl implements FarmDocManagerInter {
	private static final Logger log = Logger.getLogger(FarmDocManagerImpl.class);
	@Resource
	private FarmDocTypeInter farmDocTypeManagerImpl;
	@Resource
	private FarmDocDaoInter farmDocDao;
	@Resource
	private FarmDocfileDaoInter farmDocfileDao;
	@Resource
	private FarmDoctextDaoInter farmDoctextDao;
	@Resource
	private FarmTypePopServerInter farmTypePopServerImpl;
	@Resource
	private FarmRfDoctextfileDaoInter farmRfDoctextfileDao;
	@Resource
	private FarmRfDoctypeDaoInter farmRfDoctypeDao;
	@Resource
	private FarmDoctypeDaoInter farmDoctypeDao;
	@Resource
	private FarmDocruninfoDaoInter farmDocruninfoDao;
	@Resource
	private FarmDocenjoyDaoInter farmDocenjoyDao;
	@Resource
	private FarmDocOperateRightInter farmDocOperate;
	@Resource
	private FarmDocgroupManagerInter farmDocgroupManagerImpl;
	@Resource
	private FarmDocruninfoDetailDaoInter farmDocruninfoDetailDao;
	@Resource
	private FarmDocRunInfoInter farmDocRunInfoImpl;
	@Resource
	private FarmFileManagerInter farmFileServer;
	@Resource
	private FarmtopDaoInter farmtopDaoImpl;
	@Resource
	private FarmDocOperateRightInter farmDocOperateRightImpl;
	@Resource
	private UsermessageServiceInter usermessageServiceImpl;
	@Resource
	private FarmDocIndexInter farmDocIndexManagerImpl;
	@Resource
	private FarmFileIndexManagerInter farmFileIndexManagerImpl;

	@Transactional
	public DocEntire createDoc(DocEntire entity, LoginUser user) {
		try {
			{// ??????????????????
				FarmDocruninfo runinfo = new FarmDocruninfo();
				runinfo.setHotnum(0);
				runinfo.setLastvtime(TimeTool.getTimeDate14());
				runinfo.setPraiseno(0);
				runinfo.setPraiseyes(0);
				runinfo.setVisitnum(0);
				runinfo.setAnsweringnum(0);
				runinfo.setEvaluate(0);
				runinfo = farmDocruninfoDao.insertEntity(runinfo);
				entity.getDoc().setRuninfoid(runinfo.getId());
				entity.setRuninfo(runinfo);
			}
			{// ?????????text
				entity.getTexts().setPstate("1");// 1???????????????2.????????????
				entity.getTexts().setPcontent("CREAT");
				entity.setTexts(farmDoctextDao.insertEntity(entity.getTexts()));
				entity.getDoc().setTextid(entity.getTexts().getId());
			}

			{// ??????doc??????
				// ??????????????????????????????????????????
				if (entity.getDoc().getDocdescribe() == null || entity.getDoc().getDocdescribe().trim().length() <= 0) {
					if (entity.getTexts() != null) {
						String html = HtmlUtils.HtmlRemoveTag(entity.getTexts().getText1());
						entity.getDoc().setDocdescribe(html.length() > 128 ? html.substring(0, 128) : html);
					}
				}
				// ???????????? tags?????????tags
				if (entity.getDoc().getTagkey() == null || entity.getDoc().getTagkey().trim().length() <= 0) {// ????????????tag
					entity.getDoc().setTagkey(DocTagUtils.getTags(entity.getTexts().getText1()));
				}
				entity.getDoc().setCtime(TimeTool.getTimeDate14());
				entity.getDoc().setEtime(TimeTool.getTimeDate14());
				entity.getDoc().setCuser(user.getId());
				entity.getDoc().setEuser(user.getId());
				entity.getDoc().setCusername(user.getName());
				entity.getDoc().setEusername(user.getName());
				entity.getDoc().setPubtime(
						entity.getDoc().getPubtime().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""));
				if (entity.getDoc().getReadpop() == null) {
					entity.getDoc().setReadpop(POP_TYPE.PUB.getValue());
				}
				if (entity.getDoc().getWritepop() == null) {
					entity.getDoc().setWritepop(POP_TYPE.PUB.getValue());
				}
				if (entity.getDoc().getImgid() != null && !entity.getDoc().getImgid().isEmpty()) {
					farmFileServer.submitFile(entity.getDoc().getImgid());
				}
				if (entity.getDoc().getAuthor() == null || entity.getDoc().getAuthor().isEmpty()) {
					entity.getDoc().setAuthor(user.getName());
				}
				if (entity.getType() != null && !entity.getType().getAuditpop().equals("0")) {
					// ?????????????????????????????????
					entity.getDoc().setState("2");
				}
				entity.setDoc(farmDocDao.insertEntity(entity.getDoc()));
			}

			{// audit
				if (entity.getType() != null && !entity.getType().getAuditpop().equals("0")) {
					DocAudit audit = farmTypePopServerImpl.auditHandleForDocCreate(entity.getDoc().getId(),
							entity.getTexts().getId(), user);
					entity.setAudit(audit);
					entity.setAuditTemp(entity.getTexts());
					usermessageServiceImpl.sendMessage(
							farmDocTypeManagerImpl.getPopTypeAudtUserIds(entity.getType().getId()),
							farmTypePopServerImpl.getMessageForNeedAudit(entity.getDoc().getTitle()),
							farmTypePopServerImpl.getMessageTitleForNeedAudit(entity.getDoc().getTitle()));
				}
			}
			{// ??????????????????
				if (entity.getType() != null) {
					farmRfDoctypeDao.insertEntity(new FarmRfDoctype(entity.getType().getId(), entity.getDoc().getId()));
				}
			}
			entity.setDoc(farmDocDao.insertEntity(entity.getDoc()));
			{
				// ???????????????????????????????????????
				List<String> files = FarmDocFiles.getFilesIdFromHtml(entity.getTexts().getText1());
				for (String id : files) {
					farmRfDoctextfileDao.insertEntity(new FarmRfDoctextfile(entity.getDoc().getId(), id));
					farmFileServer.submitFile(id);
				}
				// ??????????????????????????????????????????
				List<FarmDocfile> files2 = entity.getFiles();
				for (FarmDocfile file : files2) {
					farmRfDoctextfileDao.insertEntity(new FarmRfDoctextfile(entity.getDoc().getId(), file.getId()));
					farmFileServer.submitFile(file.getId());
				}
			}
			{
				// ????????????
				if (entity.getDoc().getDocgroupid() != null && !entity.getDoc().getDocgroupid().isEmpty()) {
					entity.setGroup(farmDocgroupManagerImpl.getFarmDocgroup(entity.getDoc().getDocgroupid()));
				}
			}
			{// ???????????????????????????,????????????
				if (entity.getType() == null || entity.getType().getAuditpop().equals("0")) {
					farmDocIndexManagerImpl.addLuceneIndex(entity);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("farmDoc?????????????????????" + e + e.getMessage());
		}
		return entity;
	}

	@Transactional
	public DocEntire editDocByUser(DocEntire entity, String editNote, LoginUser user) throws CanNoWriteException {
		Doc entity2 = farmDocDao.getEntity(entity.getDoc().getId());
		if (!farmDocOperate.isWrite(user, entity2)) {
			throw new CanNoWriteException();
		}
		return editDoc(entity, editNote, user);
	}

	@Transactional
	public DocEntire editDoc(DocEntire paradoce, String editNote, LoginUser user) {
		DocEntire newDoce = getDoc(paradoce.getDoc().getId());
		DocEntire oldDoce = null;
		try {
			oldDoce = (DocEntire) BeanUtils.cloneBean(newDoce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		newDoce.getDoc().setEtime(TimeTool.getTimeDate14());
		newDoce.getDoc().setEuser(user.getId());
		newDoce.getDoc().setEusername(user.getName());
		if (paradoce.getDoc().getPcontent() != null) {
			newDoce.getDoc().setPcontent(paradoce.getDoc().getPcontent());
		}
		if (paradoce.getDoc().getReadpop() != null) {
			newDoce.getDoc().setReadpop(paradoce.getDoc().getReadpop());
		}
		if (paradoce.getDoc().getWritepop() != null) {
			newDoce.getDoc().setWritepop(paradoce.getDoc().getWritepop());
		}
		if (paradoce.getDoc().getState() != null) {
			newDoce.getDoc().setState(paradoce.getDoc().getState());
		}
		if (paradoce.getDoc().getDocgroupid() != null) {
			newDoce.getDoc().setDocgroupid(paradoce.getDoc().getDocgroupid());
		}
		if (paradoce.getDoc().getImgid() != null && !paradoce.getDoc().getImgid().isEmpty()) {
			newDoce.getDoc().setImgid(paradoce.getDoc().getImgid());
		}
		if (paradoce.getDoc().getTopleve() != null) {
			newDoce.getDoc().setTopleve(paradoce.getDoc().getTopleve());
		}
		if (paradoce.getDoc().getPubtime() != null) {
			newDoce.getDoc().setPubtime(
					paradoce.getDoc().getPubtime().replaceAll(" ", "").replaceAll("-", "").replaceAll(":", ""));
		}
		if (paradoce.getDoc().getSource() != null) {
			newDoce.getDoc().setSource(paradoce.getDoc().getSource());
		}
		if (paradoce.getDoc().getTagkey() != null) {
			newDoce.getDoc().setTagkey(paradoce.getDoc().getTagkey());
		}
		if (paradoce.getDoc().getShorttitle() != null) {
			newDoce.getDoc().setShorttitle(paradoce.getDoc().getShorttitle());
		}
		if (paradoce.getDoc().getDomtype() != null) {
			newDoce.getDoc().setDomtype(paradoce.getDoc().getDomtype());
		}
		if (newDoce.getDoc().getAuthor() == null) {
			newDoce.getDoc().setAuthor(newDoce.getDoc().getCusername());
		}
		if (paradoce.getDoc().getAuthor() != null) {
			newDoce.getDoc().setAuthor(paradoce.getDoc().getAuthor());
		}
		if (paradoce.getTexts() != null) {
			String html = HtmlUtils.HtmlRemoveTag(paradoce.getTexts().getText1());
			newDoce.getDoc().setDocdescribe(html.length() > 128 ? html.substring(0, 128) : html);
		}
		if (!paradoce.getDoc().getDocdescribe().isEmpty()) {
			newDoce.getDoc().setDocdescribe(paradoce.getDoc().getDocdescribe());
		}
		if (paradoce.getDoc().getTitle() != null) {
			newDoce.getDoc().setTitle(paradoce.getDoc().getTitle());
		}

		FarmDoctype oldType = farmRfDoctypeDao.getDocType(paradoce.getDoc().getId());
		FarmDoctype newType = paradoce.getType();
		if(newType!=null){
			newDoce.setType(newType);
			List<FarmDoctype> typelist = new ArrayList<FarmDoctype>();
			typelist.add(newType);
			newDoce.setCurrenttypes(typelist);
		}
		if (newType == null) {
			newType = oldType;
		}
		farmDocDao.editEntity(newDoce.getDoc());
		// ??????????????????
		{
			// ??????????????????(?????????????????????????????????????????????????????????????????????????????????)
			if (newType != null && !newType.getAuditpop().equals("0")) {
				editDocAuditHandle(newType, oldType, newDoce, paradoce, user, editNote);

			} else {
				// ???????????????,????????????????????????
				usermessageServiceImpl.sendMessage(farmDocRunInfoImpl.getDocJoinUserIds(newDoce.getDoc().getId()),
						"?????????" + newDoce.getDoc().getTitle() + "?????????????????????", "??????????????????");
				insertDocTextVersionForCurrent(newDoce, paradoce, user, editNote);
			}
		}
		{
			// ?????????????????????????????????
			List<String> files = FarmDocFiles.getFilesIdFromHtml(paradoce.getTexts().getText1());
			for (String id : files) {
				// ????????????????????????
				List<DBRule> listRules = new ArrayList<DBRule>();
				listRules.add(new DBRule("FILEID", id, "="));
				listRules.add(new DBRule("DOCID", newDoce.getDoc().getId(), "="));

				FarmDocfile file = farmFileServer.getFile(id);
				if (file != null) {
					farmRfDoctextfileDao.deleteEntitys(listRules);
					farmRfDoctextfileDao.insertEntity(new FarmRfDoctextfile(newDoce.getDoc().getId(), id));
					// ????????????
					farmFileServer.submitFile(id);
				} else {
					log.error("file(" + id + ") not exist at database!");
				}
			}
			// ??????????????????????????????????????????
			List<FarmDocfile> files2 = paradoce.getFiles();
			for (FarmDocfile file : files2) {
				farmRfDoctextfileDao.insertEntity(new FarmRfDoctextfile(newDoce.getDoc().getId(), file.getId()));
				// ????????????
				farmFileServer.submitFile(file.getId());
			}
		}
		// ????????????
		List<FarmRfDoctype> list = farmRfDoctypeDao
				.selectEntitys(new DBRule("DOCID", paradoce.getDoc().getId(), "=").getDBRules());
		if (paradoce.getType() != null) {
			if (list != null && list.size() > 0) {
				// ???????????????
				FarmRfDoctype farmRfDoctype = list.get(0);
				farmRfDoctype.setTypeid(paradoce.getType().getId());
				farmRfDoctypeDao.editEntity(farmRfDoctype);
			} else {
				// ??????????????????????????????
				farmRfDoctypeDao.insertEntity(new FarmRfDoctype(paradoce.getType().getId(), paradoce.getDoc().getId()));
			}
		}

		// ???????????????????????????
		if (newType == null || newType.getAuditpop().equals("0")) {
			// ???????????????????????????????????????????????????????????????
			farmFileIndexManagerImpl.handleFileLucenneIndexBymoveType(oldDoce, newDoce);
			// ??????????????????
			farmDocIndexManagerImpl.reLuceneIndex(oldDoce, newDoce);
		} else {
			// ????????????????????????????????????????????????????????????????????????
			if (!newType.getId().equals(oldType.getId())) {
				farmDocIndexManagerImpl.delLuceneIndex(oldDoce);
			}
		}
		return newDoce;
	}

	/**
	 * audit
	 * 
	 * @param newType
	 * @param oldType
	 * @param newDoce
	 * @param paradoce
	 * @param user
	 * @param editNote
	 */
	private void editDocAuditHandle(FarmDoctype newType, FarmDoctype oldType, DocEntire newDoce, DocEntire paradoce,
			LoginUser user, String editNote) {
		// ????????????
		FarmDoctext newText = null;
		// ?????????????????????????????????
		if (newType.getId().equals(oldType.getId())) {
			// ???????????????????????????(????????????????????????????????????????????????)
			newText = insertDocTextVersionForTemp(newDoce, paradoce, user, editNote);
		} else {
			// ????????????????????????
			// ??????????????????.????????????????????????????????? ???????????? ?????????
			newText = insertDocTextVersionForTemp(newDoce, paradoce, user, editNote);
			newDoce.getDoc().setState("2");
			farmDocDao.editEntity(newDoce.getDoc());
			farmDocIndexManagerImpl.delLuceneIndex(paradoce);
		}
		DocAudit audit = farmTypePopServerImpl.auditHandleForDocEdit(paradoce.getDoc().getId(), newText.getId(), user);
		newDoce.setAudit(audit);
		newDoce.setAuditTemp(newText);
		usermessageServiceImpl.sendMessage(farmDocTypeManagerImpl.getPopTypeAudtUserIds(newType.getId()),
				farmTypePopServerImpl.getMessageForNeedAudit(newDoce.getDoc().getTitle()),
				farmTypePopServerImpl.getMessageTitleForNeedAudit(newDoce.getDoc().getTitle()));
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????
	 * 
	 * @param oldText
	 *            ???????????????
	 * @param newText
	 *            ?????????
	 * @param user
	 *            ????????????
	 * @param message
	 *            ????????????
	 * @return ???????????? ?????????
	 */
	private FarmDoctext insertDocTextVersionForCurrent(DocEntire dbText, DocEntire newText, LoginUser user,
			String message) {
		FarmDoctext text = farmDoctextDao.getEntity(dbText.getDoc().getTextid());
		try {
			FarmDoctext histext = (FarmDoctext) BeanUtils.cloneBean(text);
			histext.setId(null);
			histext.setPstate("2");
			histext.setDocid(dbText.getDoc().getId());
			farmDoctextDao.insertEntity(histext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		text.setText1(newText.getTexts().getText1());
		text.setCtime(TimeTool.getTimeDate14());
		text.setEtime(TimeTool.getTimeDate14());
		text.setCuser(user.getId());
		text.setPcontent(message);
		text.setCusername(user.getName());
		text.setEuser(user.getId());
		text.setEusername(user.getName());
		farmDoctextDao.editEntity(text);
		return newText.getTexts();
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param oldText
	 *            ???????????????
	 * @param newText
	 *            ?????????
	 * @param user
	 *            ????????????
	 * @param message
	 *            ????????????
	 * @return
	 */
	private FarmDoctext insertDocTextVersionForTemp(DocEntire oldText, DocEntire newText, LoginUser user,
			String message) {
		// ?????????????????????????????????
		FarmDoctext text = farmDoctextDao.getEntity(oldText.getDoc().getTextid());
		FarmDoctext newtext;
		try {
			if (text == null) {
				text = new FarmDoctext();
			}
			FarmDoctext histext = (FarmDoctext) BeanUtils.cloneBean(text);
			histext.setId(null);
			// 1???????????????2.???????????????3?????????
			histext.setPstate("3");
			histext.setDocid(oldText.getDoc().getId());
			histext.setText1(newText.getTexts().getText1());
			histext.setCtime(TimeTool.getTimeDate14());
			histext.setEtime(TimeTool.getTimeDate14());
			histext.setCuser(user.getId());
			histext.setPcontent(message);
			histext.setCusername(user.getName());
			histext.setEuser(user.getId());
			histext.setEusername(user.getName());
			newtext = farmDoctextDao.insertEntity(histext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return newtext;
	}

	@Transactional
	public DocEntire deleteDoc(String entity, LoginUser user) throws CanNoDeleteException {
		Doc doc = farmDocDao.getEntity(entity);
		if (!farmDocOperate.isDel(user, doc)) {
			throw new CanNoDeleteException("???????????????????????????");
		}
		return deleteDocNoPop(entity, user);
	}

	@Override
	public DataQuery createSimpleDocQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery.init(query,
				"FARM_DOC  A LEFT JOIN FARM_RF_DOCTYPE B ON B.DOCID =A.ID LEFT JOIN FARM_DOCTYPE C ON C.ID=B.TYPEID LEFT JOIN farm_docgroup d ON d.ID=a.DOCGROUPID",
				"A.ID AS ID,A.DOCDESCRIBE as DOCDESCRIBE,A.WRITEPOP as WRITEPOP,A.READPOP as READPOP,A.TITLE AS TITLE,A.AUTHOR AS AUTHOR,A.PUBTIME AS PUBTIME,A.DOMTYPE AS DOMTYPE,A.SHORTTITLE AS SHORTTITLE,A.TAGKEY AS TAGKEY,A.STATE AS STATE,D.GROUPNAME AS GROUPNAME ");
		return dbQuery;
	}

	@Override
	@Transactional
	public DocEntire getDoc(String docid) {
		if (docid == null || docid.isEmpty()) {
			return null;
		}
		DocEntire doce = new DocEntire(farmDocDao.getEntity(docid));
		doce.setTexts(farmDoctextDao.getEntity(doce.getDoc().getTextid()));
		List<FarmDoctype> types = farmRfDoctypeDao.getDocTypes(docid);
		if (types.size() > 0) {
			doce.setType(types.get(0));
			doce.setCurrenttypes(farmDocTypeManagerImpl.getTypeAllParent(doce.getType().getId()));
		}
		doce.setRuninfo(farmDocruninfoDao.getEntity(doce.getDoc().getRuninfoid()));
		if (doce.getDoc().getTagkey() != null) {
			String tags = doce.getDoc().getTagkey();
			String[] tags1 = tags.trim().replaceAll("???", ",").replaceAll("???", ",").split(",");
			doce.setTags(Arrays.asList(tags1));
		}
		if (doce.getDoc().getDocgroupid() != null) {
			doce.setGroup(farmDocgroupManagerImpl.getFarmDocgroup(doce.getDoc().getDocgroupid()));
			doce.getGroup().setImgurl(farmFileServer.getFileURL(doce.getGroup().getGroupimg()));
		}
		// ????????????
		List<FarmDocfile> files = farmDocfileDao.getEntityByDocId(docid);
		for (FarmDocfile file : files) {
			file.setUrl(farmFileServer.getFileURL(file.getId()));
		}
		doce.setFiles(files);
		if (doce.getDoc().getImgid() != null && doce.getDoc().getImgid().trim().length() > 0) {
			String url = DocumentConfig.getString("config.doc.download.url") + doce.getDoc().getImgid();
			doce.setImgUrl(url);
		}
		// ??????????????????
		{
			User user = new User();
			user.setId(doce.getDoc().getCuser());
			user.setName(doce.getDoc().getCusername());
			doce.setUser(user);
		}
		// ??????????????????
		{
			if (doce.getType() != null) {
				List<Doctypepop> pops = farmDocTypeManagerImpl.getTypePops(doce.getType().getId());
				List<Doctypepop> readpops = new ArrayList<>();
				List<Doctypepop> writepops = new ArrayList<>();
				List<Doctypepop> auditpops = new ArrayList<>();
				for (Doctypepop node : pops) {
					if (node.getFuntype().equals("1")) {
						// ??????
						readpops.add(node);
					}
					if (node.getFuntype().equals("2")) {
						// ??????
						writepops.add(node);
					}
					if (node.getFuntype().equals("3")) {
						// ??????
						auditpops.add(node);
					}
				}
				doce.setTypeReadPops(readpops);
				doce.setTypeWritePops(writepops);
				doce.setTypeAuditPops(auditpops);
			}
		}
		return doce;
	}

	@Override
	@Transactional
	public DocEntire getDoc(String docid, LoginUser user) throws CanNoReadException, DocNoExistException {
		Doc docbean = farmDocDao.getEntity(docid);
		if (docbean == null) {
			{
				// ??????????????????????????????????????????
				List<TypeBrief> types = farmDocTypeManagerImpl.getPubTypes();
				List<String> types_str = new ArrayList<>();
				for (TypeBrief type : types) {
					types_str.add(type.getId());
				}
				List<FarmDocgroup> groups = farmDocgroupManagerImpl.getAllGroup();
				List<String> groups_str = new ArrayList<>();
				for (FarmDocgroup group : groups) {
					groups_str.add(group.getId());
				}
				farmDocIndexManagerImpl.delLuceneIndex(docid, types_str, groups_str);
			}
			throw new DocNoExistException();
		}
		if (!farmDocOperate.isRead(user, docbean)) {
			throw new CanNoReadException();
		}
		DocEntire doc = getDoc(docid);
		return doc;
	}

	@Override
	@Transactional
	public Doc getDocOnlyBean(String id) {
		return farmDocDao.getEntity(id);
	}

	@Override
	public DataQuery createSimpleTypeQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery.init(query, "farm_doctype a LEFT JOIN farm_doctype b ON a.PARENTID=b.id",
				"a.ID AS id,a.READPOP as READPOP,a.WRITEPOP as WRITEPOP,a.AUDITPOP as AUDITPOP,a.NAME AS NAME,a.TYPEMOD AS TYPEMOD,a.CONTENTMOD AS CONTENTMOD,a.SORT AS SORT,a.TYPE AS TYPE, a.METATITLE AS METATITLE,a.METAKEY AS METAKEY,a.METACONTENT AS METACONTENT,a.LINKURL AS LINKURL,a.PCONTENT AS PCONTENT,a.PSTATE AS PSTATE");
		return dbQuery;
	}

	@Override
	@Transactional
	public void updateDocTypeOnlyOne(String docid, String typeId) {
		List<DBRule> list = new ArrayList<DBRule>();
		list.add(new DBRule("DOCID", docid, "="));
		farmRfDoctypeDao.deleteEntitys(list);
		farmRfDoctypeDao.insertEntity(new FarmRfDoctype(typeId, docid));
	}

	// WHERE PSTATE='2' AND DOCID=''
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<FarmDoctext> getDocVersions(String docId) {
		FarmDoctext docText = farmDoctextDao.getEntity(getDocOnlyBean(docId).getTextid());
		DataQuery dbQuery = DataQuery.getInstance("1", "ID,ETIME,CUSERNAME,DOCID,PCONTENT,PSTATE", "farm_doctext");
		dbQuery.addRule(new DBRule("PSTATE", "1", "!="));
		dbQuery.addRule(new DBRule("DOCID", docId, "="));
		dbQuery.addSqlRule("and PSTATE !='3'");
		dbQuery.setPagesize(100);
		dbQuery.addSort(new DBSort("CTIME", "DESC"));
		dbQuery.setNoCount();
		@SuppressWarnings("rawtypes")
		List list = new ArrayList();
		list.add(docText);
		try {
			list.addAll(dbQuery.search().getObjectList(FarmDoctext.class));
		} catch (SQLException e) {
			throw new RuntimeException();
		}
		return list;
	}

	@Override
	@Transactional
	public DocEntire getDocVersion(String textId, LoginUser currentUser) {
		if (textId == null) {
			return null;
		}
		FarmDoctext text = farmDoctextDao.getEntity(textId);
		DocEntire doc = new DocEntire(farmDocDao.getEntity(text.getDocid()));
		if (doc.getDoc().getReadpop().equals("0") && !doc.getDoc().getCuser().equals(currentUser.getId())) {
			throw new RuntimeException("??????????????????");
		}
		doc.setTexts(text);

		List<FarmDoctype> doctypes = farmRfDoctypeDao.getDocTypes(doc.getDoc().getId());
		if (doctypes.size() > 0) {
			doc.setType(doctypes.get(0));
			doc.setCurrenttypes(farmDocTypeManagerImpl.getTypeAllParent(doctypes.get(0).getId()));
		}
		if (doc.getDoc().getTagkey() != null) {
			String tags = doc.getDoc().getTagkey();
			String[] tags1 = tags.trim().replaceAll("???", ",").replaceAll("???", ",").split(",");
			doc.setTags(Arrays.asList(tags1));
		}
		return doc;
	}

	@Override
	@Transactional
	public void delImg(String imgid) {
		// farmDocfileDao.deleteEntity(farmDocfileDao.getEntity(imgid));
		// ??????????????????????????????
		List<Doc> docList = farmDocDao.selectEntitys(new DBRule("IMGID", imgid, "=").getDBRules());

		for (Doc doc : docList) {
			doc.setImgid(null);
		}

		// ??????????????????????????????
		farmRfDoctextfileDao.deleteEntitys(new DBRule("FILEID", imgid, "=").getDBRules());

		// ????????????????????????
		farmFileServer.delFile(imgid, null);
	}

	@Override
	@Transactional
	public DocEntire deleteDocNoPop(String entity, LoginUser user) throws CanNoDeleteException {
		DocEntire doc = getDoc(entity);
		FarmDoctext text = farmDoctextDao.getEntity(doc.getDoc().getTextid());
		farmTypePopServerImpl.delAuditInfo(doc.getDoc().getId());
		// ????????????
		farmtopDaoImpl.deleteEntitys(new DBRule("docid", entity, "=").getDBRules());
		// ????????????
		List<DBRule> joylist = new ArrayList<DBRule>();
		joylist.add(new DBRule("DOCID", doc.getDoc().getId(), "="));
		farmDocenjoyDao.deleteEntitys(joylist);
		// ?????????????????????
		List<DBRule> rulesDelType = new ArrayList<DBRule>();
		rulesDelType.add(new DBRule("DOCID", doc.getDoc().getId(), "="));
		farmRfDoctypeDao.deleteEntitys(rulesDelType);
		// ????????????
		farmDocDao.deleteEntity(doc.getDoc());
		// ?????????????????????
		List<DBRule> rulesDelFile = new ArrayList<DBRule>();
		rulesDelFile.add(new DBRule("DOCID", doc.getDoc().getId(), "="));
		List<FarmRfDoctextfile> files = farmRfDoctextfileDao.selectEntitys(rulesDelFile);
		for (FarmRfDoctextfile node : files) {
			farmFileServer.delFile(node.getFileid(), user);
		}
		// ?????????????????????????????????
		List<DBRule> rulesDelText = new ArrayList<DBRule>();
		rulesDelText.add(new DBRule("DOCID", doc.getDoc().getId(), "="));
		farmRfDoctextfileDao.deleteEntitys(rulesDelText);
		// ????????????
		if (doc.getDoc().getImgid() != null && doc.getDoc().getImgid().trim().length() > 0) {
			farmDocfileDao.deleteEntity(farmDocfileDao.getEntity(doc.getDoc().getImgid()));
		}
		// ??????????????????
		{
			List<DBRule> rulesDelruninfoDetail = new ArrayList<DBRule>();
			rulesDelruninfoDetail.add(new DBRule("RUNINFOID", doc.getDoc().getRuninfoid(), "="));
			farmDocruninfoDetailDao.deleteEntitys(rulesDelruninfoDetail);
		}
		// ??????????????????
		farmDocruninfoDao.deleteEntity(farmDocruninfoDao.getEntity(doc.getDoc().getRuninfoid()));
		// ?????????????????????
		farmDoctextDao.deleteEntity(text);
		farmDocIndexManagerImpl.delLuceneIndex(doc);
		return doc;
	}

	@Override
	public DataQuery createAllPubDocQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery.init(query,
				"FARM_DOC  A LEFT JOIN FARM_RF_DOCTYPE B ON B.DOCID =A.ID LEFT JOIN FARM_DOCTYPE C ON C.ID=B.TYPEID LEFT JOIN farm_docgroup d ON d.ID=a.DOCGROUPID",
				"A.ID AS ID,A.DOCDESCRIBE as DOCDESCRIBE,A.WRITEPOP as WRITEPOP,A.READPOP as READPOP,A.TITLE AS TITLE,A.AUTHOR AS AUTHOR,A.PUBTIME AS PUBTIME,A.DOMTYPE AS DOMTYPE,A.SHORTTITLE AS SHORTTITLE,A.TAGKEY AS TAGKEY,A.STATE AS STATE,D.GROUPNAME AS GROUPNAME ");
		dbQuery.addRule(new DBRule("c.READPOP", "0", "="));
		dbQuery.addRule(new DBRule("a.READPOP", "1", "="));
		return dbQuery;
	}

	@Override
	@Transactional
	public void move2Type(String docIds, String typeId) {
		if (docIds == null || docIds.isEmpty()) {
			throw new RuntimeException("?????????????????????docIds");
		}
		if (typeId == null || typeId.isEmpty()) {
			throw new RuntimeException("?????????????????????typeId");
		}

		String[] docIdsArr = docIds.split(",");
		for (String docId : docIdsArr) {
			List<FarmRfDoctype> list = farmRfDoctypeDao.selectEntitys(new DBRule("DOCID", docId, "=").getDBRules());
			if (list.size() > 0) {
				FarmRfDoctype farmRfDoctype = list.get(0);
				farmRfDoctype.setTypeid(typeId);
				farmRfDoctypeDao.editEntity(farmRfDoctype);
			}
		}
	}

}
