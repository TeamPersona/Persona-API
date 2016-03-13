CREATE TABLE IF NOT EXISTS accounts
(
    id SERIAL PRIMARY KEY,
    given_name TEXT NOT NULL,
    family_name TEXT NOT NULL,
    email_address TEXT UNIQUE,
    phone_number TEXT UNIQUE,
    reward_points INTEGER CHECK(reward_points >= 0),
    balance INTEGER CHECK(balance >= 0)
);

CREATE TABLE IF NOT EXISTS passwords
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS google_accounts
(
    id SERIAL PRIMARY KEY REFERENCES accounts,
    google_id TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS demo_message
(
    offerid INT PRIMARY KEY
    message TEXT,
    timestamp TIMESTAMP
);

CREATE TABLE IF NOT EXISTS msg_history
(
    msg_id SERIAL PRIMARY KEY,
    offerid UUID NOT NULL,
    userid TEXT NOT NULL,
    sender TEXT,
    message TEXT,
    timestamp TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_msghistory ON msg_history (offerid, userid);

CREATE TABLE IF NOT EXISTS msg_ack
(
    offerid UUID NOT NULL,
    userid TEXT NOT NULL,
    timestamp TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_msg_ack ON msg_ack (offerid, userid);

CREATE TABLE IF NOT EXISTS chat_offers
(
  offerid UUID PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS third_party_accounts
(
    id TEXT PRIMARY KEY,
    secret TEXT NOT NULL,
    creation_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authorization_codes
(
    code TEXT PRIMARY KEY,
    account_id INT REFERENCES accounts,
    third_party_account_id TEXT REFERENCES third_party_accounts,
    valid BOOLEAN
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    token TEXT PRIMARY KEY,
    account_id INT REFERENCES accounts,
    third_party_account_id TEXT REFERENCES third_party_accounts,
    valid BOOLEAN
);

CREATE SEQUENCE public.offercategory_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.offercriteriontype_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.offers_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.offerstatus_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.offertype_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.partnersid_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.picategory_picategoryid_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.pifields_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.pitype_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

  CREATE SEQUENCE public.pivaluetype_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE SEQUENCE public.rewardtier_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;


CREATE TABLE IF NOT EXISTS rewardtiers
(
  tierid bigint NOT NULL DEFAULT nextval('rewardtier_seq'::regclass),
  tier character varying,
  pointsrequired numeric,
  CONSTRAINT rewardtiers_pk PRIMARY KEY (tierid)
);

CREATE TABLE IF NOT EXISTS offerstatus
(
  offerstatusid bigint NOT NULL DEFAULT nextval('offerstatus_seq'::regclass),
  offerstatus character varying,
  CONSTRAINT offerstatus_pk PRIMARY KEY (offerstatusid)
);

CREATE TABLE IF NOT EXISTS partners
(
  partnerid bigint NOT NULL DEFAULT nextval('partnersid_seq'::regclass),
  partnername character varying,
  partnerimageurl character varying,
  CONSTRAINT partners_pk PRIMARY KEY (partnerid)
);

CREATE TABLE IF NOT EXISTS offers
(
  offerid bigint NOT NULL DEFAULT nextval('offers_seq'::regclass),
  partnerid bigint,
  maxparticipants bigint,
  startingtime timestamp without time zone,
  endingtime timestamp without time zone,
  reward numeric,
  offerstatusid bigint NOT NULL,
  mintierid bigint,
  offerdetails character varying,
  CONSTRAINT offers_pk PRIMARY KEY (offerid),
  CONSTRAINT offers_mintierid_fkey FOREIGN KEY (mintierid)
      REFERENCES public.rewardtiers (tierid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offers_offerstatusid_fkey FOREIGN KEY (offerstatusid)
      REFERENCES public.offerstatus (offerstatusid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offers_partnerid_fkey FOREIGN KEY (partnerid)
      REFERENCES public.partners (partnerid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS pivaluetypes
(
  pivaluetypeid bigint NOT NULL DEFAULT nextval('pivaluetype_seq'::regclass),
  pivaluetype character varying NOT NULL,
  CONSTRAINT pivaluetypes_pk PRIMARY KEY (pivaluetypeid)
);

CREATE TABLE IF NOT EXISTS picategory
(
  picategoryid integer NOT NULL DEFAULT nextval('picategory_picategoryid_seq'::regclass),
  category text NOT NULL,
  CONSTRAINT picategory_pkey PRIMARY KEY (picategoryid)
);

CREATE TABLE IF NOT EXISTS pifields
(
  pifieldid bigint NOT NULL DEFAULT nextval('pifields_seq'::regclass),
  pifield character varying,
  pivaluetypeid bigint,
  picategoryid integer,
  CONSTRAINT pifields_pk PRIMARY KEY (pifieldid),
  CONSTRAINT pifields_picategoryid_fkey FOREIGN KEY (picategoryid)
      REFERENCES public.picategory (picategoryid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT pifields_pivaluetypeid_fkey FOREIGN KEY (pivaluetypeid)
      REFERENCES public.pivaluetypes (pivaluetypeid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS offertypes
(
  offertypeid bigint NOT NULL DEFAULT nextval('offertype_seq'::regclass),
  offertype character varying,
  CONSTRAINT offertypes_pk PRIMARY KEY (offertypeid)
);

CREATE TABLE IF NOT EXISTS offercategories
(
  offerid integer NOT NULL,
  offertypeid integer NOT NULL,
  typeindex integer,
  CONSTRAINT offercategories_piofferid_fkey FOREIGN KEY (offerid)
      REFERENCES public.offers (offerid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offercategories_pioffertypeid_fkey FOREIGN KEY (offertypeid)
      REFERENCES public.offertypes (offertypeid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS offerparticipation
(
  offerid bigint,
  userid bigint,
  CONSTRAINT offerparticipation_accountid_fkey FOREIGN KEY (userid)
      REFERENCES public.accounts (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offerparticipation_offerid_fkey FOREIGN KEY (offerid)
      REFERENCES public.offers (offerid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS offerinforequired
(
  offerid integer NOT NULL,
  pifieldid integer NOT NULL,
  CONSTRAINT offerinforequired_pifieldid_fkey FOREIGN KEY (pifieldid)
      REFERENCES public.pifields (pifieldid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offerinforequired_piofferid_fkey FOREIGN KEY (offerid)
      REFERENCES public.offers (offerid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS offercriteriontypes
(
  offercriteriontypeid bigint NOT NULL DEFAULT nextval('offercriteriontype_seq'::regclass),
  offercriteriontype character varying,
  offercriterionsql character varying,
  CONSTRAINT offercriteriontypes_pk PRIMARY KEY (offercriteriontypeid)
);

CREATE TABLE IF NOT EXISTS offercriteria
(
  offerid bigint,
  offercriteriaindex integer,
  offercriteriasubindex integer,
  pifieldid bigint,
  offercriteriontypeid bigint,
  offercriteriastring character varying,
  offercriteriaint integer,
  offercriteriafloat numeric,
  offercriteriadate timestamp without time zone,
  CONSTRAINT offercriteria_offercriteriontypeid_fkey FOREIGN KEY (offercriteriontypeid)
      REFERENCES public.offercriteriontypes (offercriteriontypeid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offercriteria_offerid_fkey FOREIGN KEY (offerid)
      REFERENCES public.offers (offerid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT offercriteria_pifieldid_fkey FOREIGN KEY (pifieldid)
      REFERENCES public.pifields (pifieldid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE OR REPLACE VIEW public.view_offercriteria AS 
 SELECT offers.offerid,
    offercriteria.offercriteriaindex,
    offercriteria.offercriteriasubindex,
    pifields.pifield,
    offercriteriontypes.offercriteriontype,
    offercriteria.offercriteriastring,
    offercriteria.offercriteriaint,
    offercriteria.offercriteriafloat,
    offercriteria.offercriteriadate
   FROM offers
     LEFT JOIN offercriteria ON offers.offerid = offercriteria.offerid
     LEFT JOIN pifields ON offercriteria.pifieldid = pifields.pifieldid
     LEFT JOIN offercriteriontypes ON offercriteria.offercriteriontypeid = offercriteriontypes.offercriteriontypeid
  ORDER BY offers.offerid, offercriteria.offercriteriaindex, offercriteria.offercriteriasubindex;

  CREATE OR REPLACE VIEW public.view_offerdata AS 
 SELECT offers.offerid,
    partners.partnerid,
    partners.partnername,
    partners.partnerimageurl,
    offers.offerdetails,
    offers.offerstatusid,
    offers.maxparticipants,
    offers.startingtime,
    offers.endingtime,
    offers.reward,
    COALESCE(participants.numparticipants, 0::bigint) AS numparticipants,
    COALESCE(offers.mintierid, 0::bigint) AS mintierid
   FROM offers
     LEFT JOIN partners ON offers.partnerid = partners.partnerid
     LEFT JOIN ( SELECT offerparticipation.offerid,
            count(*) AS numparticipants
           FROM offerparticipation
          GROUP BY offerparticipation.offerid) participants ON offers.offerid = participants.offerid;


CREATE OR REPLACE FUNCTION public.haspoints(
    offeridin bigint,
    useridin bigint)
  RETURNS boolean AS
$BODY$
DECLARE 
  result boolean := false;
  searchsql text := '';
  userPoints int := 0;
  minPoints int := 0;
BEGIN

  searchsql := 'SELECT reward_points from accounts where id = ' || useridIN  ;
  EXECUTE searchsql into userPoints ;

  searchsql := 'SELECT COALESCE (pointsrequired,0) FROM offers LEFT JOIN rewardtiers ON offers.mintierid = rewardtiers.tierid WHERE offerid =  ' || offeridIN  ;
  EXECUTE searchsql into minPoints ;
  
  IF minPoints <= userPoints then
    result := true;
  END IF;
  return result;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.participate(
    offeridin bigint,
    useridin bigint)
  RETURNS boolean AS
$BODY$
DECLARE 
  result boolean := false;
  searchsql text := '';
  numberRows int := 0;
  offerActive boolean := false;
  offerFull boolean := false;
  userPoints int := 0;
  minPoints int := 0;
BEGIN
  searchsql := 'SELECT count(*) from offerparticipation where offerid = ' || offeridIN || ' and userid = ' || useridIN ;
  EXECUTE searchsql into numberRows ;

  searchsql := 'SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM view_offerdata INNER JOIN offerstatus ON view_offerdata.offerstatusid = offerstatus.offerstatusID WHERE offerstatus = ''Active'' AND offerid =' || offeridIN  ;
  EXECUTE searchsql into offerActive ;

  IF numberRows <> 0 then
    result := true;
  ELSIF offerActive = false then
    result := false;
  ELSE
    insert into offerparticipation values (offeridIN, useridIN);
    searchsql := 'SELECT CASE WHEN maxparticipants = numparticipants THEN true ELSE false END FROM view_offerdata where offerid = ' || offeridIN ;
    EXECUTE searchsql into offerFull ;
    IF offerFull = true THEN
      UPDATE offers set offerstatusid = 2 where offerid = offeridIN;
    END IF;
    result := true;
  END IF;

  return result;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.unparticipate(
    offeridin bigint,
    useridin bigint)
  RETURNS boolean AS
$BODY$
DECLARE 
  result boolean := false;
  searchsql text := '';
  numberRows int := 0;
  offerActive boolean := false;
  offerFull boolean := false;
BEGIN
  --searchsql := 'SELECT count(*) from offerparticipation where offerid = ' || offeridIN || ' and userid = ' || useridIN ;
  --EXECUTE searchsql into numberRows ;

  searchsql := 'SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM view_offerdata INNER JOIN offerstatus ON view_offerdata.offerstatusid = offerstatus.offerstatusID WHERE (offerstatus = ''Active'' OR offerstatus = ''Full'') AND offerid =' || offeridIN  ;
  EXECUTE searchsql into offerActive ;
    
  IF offerActive = false then
    result := false;
  ELSE
    delete from offerparticipation where offerid = offeridIN and userid = useridIN;
    searchsql := 'SELECT CASE WHEN maxparticipants <> numparticipants THEN true ELSE false END FROM view_offerdata where offerid = ' || offeridIN ;
    EXECUTE searchsql into offerFull ;
    IF offerFull = true THEN
      UPDATE offers set offerstatusid = 4 where offerid = offeridIN;
    END IF;
    result := true;
  END IF;

  return result;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.getfilters(IN offeridin bigint)
  RETURNS TABLE(category text, pifield character varying) AS
$BODY$
DECLARE 
BEGIN
  RETURN QUERY
  SELECT
       picategory.category,
       pifields.pifield
       FROM offercriteria
       INNER JOIN pifields
   ON offercriteria.pifieldid = pifields.pifieldid
       INNER JOIN picategory
   ON picategory.picategoryid = pifields.picategoryid
       WHERE offercriteria.offercriteriasubindex = 1
       AND offercriteria.offerid = offeridin;

END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.getparticipating(
    IN offeridin bigint,
    IN useridin bigint)
  RETURNS TABLE(participating boolean) AS
$BODY$
DECLARE 
BEGIN
return query
  SELECT CASE WHEN part >= 1 THEN TRUE ELSE FALSE END 
  FROM (SELECT COUNT(*) as part 
  FROM offerparticipation 
  WHERE offerid = offerid
  AND userid = useridin) as participating;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.getrequiredinfo(IN offeridin bigint)
  RETURNS TABLE(category text, pifield character varying) AS
$BODY$
DECLARE 
BEGIN
return query
  SELECT
       picategory.category,
       pifields.pifield
       FROM offerinforequired
       INNER JOIN pifields
  ON offerinforequired.pifieldid = pifields.pifieldid
       INNER JOIN picategory
  ON picategory.picategoryid = pifields.picategoryid
       WHERE offerinforequired.offerid = offeridin;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.gettypes(IN offeridin bigint)
  RETURNS TABLE(offertype character varying) AS
$BODY$
DECLARE 
BEGIN
return query
  SELECT offertypes.offertype
  FROM offercategories
  INNER JOIN offertypes
  ON offercategories.offertypeid = offertypes.offertypeid
       WHERE offercategories.offerid =  offeridin
       ORDER BY offercategories.typeindex;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.insertcategories(
    offerid bigint,
    offercategories character varying)
  RETURNS void AS
$BODY$
DECLARE 
  sqlString text := '';
  categories RECORD;
BEGIN
offercategories := '''' || replace(offercategories, ',', ''',''') || '''';

sqlString := 'SELECT offertypeid ,row_number() OVER () as orderid from offertypes where offertype in (' || offercategories || ')';
  
FOR categories IN EXECUTE(sqlString) LOOP
  INSERT INTO offercategories (offerid, offertypeid, typeindex) VALUES (offerid, categories.offertypeid, categories.orderid);
END LOOP;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.insertcriteria(
    offerid bigint,
    offercriteria character varying)
  RETURNS void AS
$BODY$
DECLARE 
  sqlString text := '';
  offerCriterion RECORD;
BEGIN
offerCriteria := '''' || replace(offerCriteria, ',', ''',''') || '''';

sqlString := 'SELECT pifieldid, row_number() OVER () as orderid FROM pifields WHERE pifield IN (' || offerCriteria || ')';
  
FOR offerCriterion IN EXECUTE(sqlString) LOOP
  INSERT INTO offercriteria (offerid, offercriteriaindex, offercriteriasubindex, pifieldid, offercriteriontypeid) VALUES (offerid, offerCriterion.orderid, 1, offerCriterion.pifieldid, 4);
END LOOP;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.insertinforequired(
    offerid bigint,
    offerinforeq character varying)
  RETURNS void AS
$BODY$
DECLARE 
  sqlString text := '';
  offerInfoReqs RECORD;
BEGIN
offerInfoReq := '''' || replace(offerInfoReq, ',', ''',''') || '''';

sqlString := 'SELECT pifieldid FROM pifields WHERE pifield IN (' || offerInfoReq || ')';
  
FOR offerInfoReqs IN EXECUTE(sqlString) LOOP
  INSERT INTO offerinforequired (offerid, pifieldid) VALUES (offerid, offerInfoReqs.pifieldid);
END LOOP;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION public.createoffer(
    partnerid bigint,
    maxparticipants bigint,
    startingtime timestamp without time zone,
    endingtime timestamp without time zone,
    reward numeric,
    offerstatusid bigint,
    mintierid bigint,
    offerdetails character varying,
    offercategories character varying,
    offerinforeq character varying,
    offercriteria character varying)
  RETURNS bigint AS
$BODY$
DECLARE 
  newOfferID bigint := 5;
BEGIN

-- insert into the offer table
IF mintierid = 0 then
INSERT INTO offers (partnerid, maxparticipants, startingtime, endingtime, reward, offerstatusid, offerdetails) 
  VALUES(partnerid, maxparticipants, startingtime, endingtime, reward, offerstatusid, offerdetails) RETURNING offerid into newOfferID;
else
  INSERT INTO offers (partnerid, maxparticipants, startingtime, endingtime, reward, offerstatusid, mintierid, offerdetails) 
  VALUES(partnerid, maxparticipants, startingtime, endingtime, reward, offerstatusid, mintierid, offerdetails) RETURNING offerid into newOfferID;
end if;

PERFORM insertcategories(newOfferID, offercategories);
PERFORM insertinforequired(newOfferID, offerinforeq);
PERFORM insertcriteria(newOfferID, offercriteria);

Return newOfferID;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE;


