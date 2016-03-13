INSERT INTO third_party_accounts(id, secret, creation_time) VALUES
(
    '11205499856203749022.apps.uwpersona.com',
    'rgr3a23lf60uc6va317l3ogtsni9gumumbli0k9mqamho7313ag1',
    '2016-02-27 21:28:39.214'
);

INSERT INTO public.rewardtiers(tier, pointsrequired) VALUES ('silver', 250), ('gold', 1000);

INSERT INTO public.offerstatus (offerstatus) VALUES ('Not Started'), ('Full'), ('Complete'), ('Active'), ('Cancelled');

INSERT INTO public.partners(partnername, partnerimageurl) 
VALUES ('BMW', 'https://s3.amazonaws.com/persona.assets/500px-BMW.svg.png')
	,('Air Canada', 'https://s3.amazonaws.com/persona.assets/640px-Air_Canada_Logo.svg.png')
	,('Ford', 'https://s3.amazonaws.com/persona.assets/640px-Ford_Motor_Company_Logo.svg.png')
	,('IBM', 'https://s3.amazonaws.com/persona.assets/640px-IBM_logo.svg.png')
	,('Nike', 'https://s3.amazonaws.com/persona.assets/640px-Logo_NIKE.svg.png')
	,('Best Buy', 'https://s3.amazonaws.com/persona.assets/Best_Buy_Logo.svg.png')
	,('Boeing', 'https://s3.amazonaws.com/persona.assets/Boeing-Logo.svg.png')
	,('Canadian Tire', 'https://s3.amazonaws.com/persona.assets/CT_Classic_StdKey_RGB.png')
	,('Cineplex', 'https://s3.amazonaws.com/persona.assets/Ciniplex_logo.svg.png')
	,('Costco', 'https://s3.amazonaws.com/persona.assets/Costco_Wholesale.svg.png')
	,('Disney', 'https://s3.amazonaws.com/persona.assets/Disney_Logo.png')
	,('General Motors', 'https://s3.amazonaws.com/persona.assets/General_Motors_logo.png')
	,('HP', 'https://s3.amazonaws.com/persona.assets/HP_New_Logo_2D.svg.png')
	,('McDonald''s', 'https://s3.amazonaws.com/persona.assets/McDonald''s_Golden_Arches.svg.png')
	,('Staples', 'https://s3.amazonaws.com/persona.assets/Staples.svg.png')
	,('Apple', 'https://s3.amazonaws.com/persona.assets/giant-apple-logo-bw.png')
	,('Home Depot', 'https://s3.amazonaws.com/persona.assets/home-depot-logo.png')
	,('Shopper''s Drug Mart', 'https://s3.amazonaws.com/persona.assets/shoppers-drug-mart-logo.png')
	,('Walmart', 'https://s3.amazonaws.com/persona.assets/walmart-logo.jpg');
INSERT INTO public.pivaluetypes(pivaluetype) VALUES ('String'), ('Integer'), ('Float'), ('Date');

INSERT INTO public.picategory(category) VALUES ('Basic'), ('Personal'), ('Financial'), ('Employment'), ('Health & Fitness'), ('Entertainment'), ('Social');

INSERT INTO public.pifields(pifield, pivaluetypeid, picategoryid)
    VALUES ('First Name', 1, 1)
	,('Last Name', 1, 1)
	,('Date of Birth', 1, 1)
	,('Gender', 1, 1)
	,('Ethnicity', 1, 1)
	,('Home Address', 1, 1)
	,('Allow Contact', 1, 1)

	,('Education', 1, 2)
	,('Marital Status', 1, 2)
	,('Number of Dependents', 1, 2)
	,('Political Affiliations', 1, 2)
	,('Religion', 1, 2)

	,('Salary', 1, 3)
	,('Purchase History', 1, 3)
	,('Credit Score', 1, 3)

	,('Current Employer', 1, 4)
	,('Employment History', 1, 4)

	,('Height', 1, 5)
	,('Weight', 1, 5)
	,('Has Eyewear', 1, 5)
	,('Disabilities', 1, 5)
	,('Medical History', 1, 5)
	,('Steps Takes History', 1, 5)
	,('Sleep Tracking History', 1, 5)

	,('TV Show Interests', 1, 6)
	,('Movie Interests', 1, 6)
	,('Book Interest', 1, 6)
	,('Favourite Sports Teams', 1, 6)
	,('Video Game Interests', 1, 6)

	,('Facebook Friends List', 1, 7)
	,('Facebook Statuses', 1, 7)
	,('Facebook Events', 1, 7)
	,('Twitter Following List', 1, 7)
	,('Twitter Followers List', 1, 7)
	,('Twitter Favourties', 1, 7)
	,('Twitter Retweets', 1, 7)
	,('Instagram Following List', 1, 7)
	,('Instagram Followers List', 1, 7)
	,('Instagram Photos', 1, 7)
	,('Swarm Check-ins', 1, 7)
	,('Yelp Reviews', 1, 7)
	;

	INSERT INTO public.offertypes(offertype)
    VALUES ('Automotive')
,('Auto Accessories')
,('Auto Dealers – New')
,('Auto Dealers – Used')
,('Detail & Carwash')
,('Gas Stations')
,('Motorcycle Sales & Repair')
,('Rental & Leasing')
,('Service Repair & Parts')
,('Towing')
,('Business Support & Supplies')
,('Consultants')
,('Employment Agency')
,('Marketing & Communications')
,('Office Supplies')
,('Printing & Publishing')
,('Computers & Electronics')
,('Computer Programming & Support')
,('Consumer Electronics & Accessories')
,('Construction & Contractors')
,('Blasting & Demolition')
,('Building Materials & Supplies')
,('Construction Companies')
,('Electricians')
,('Environmental Assessments')
,('Inspectors')
,('Plaster & Concrete')
,('Plumbers')
,('Roofers')
,('Education Adult & Continuing Education')
,('Early Childhood Education')
,('Educational Resources')
,('Other Educational')
,('Entertainment')
,('Artists')
,('Event Planners & Supplies')
,('Golf Courses')
,('Movies')
,('Productions')
,('Food & Dining')
,('Catering & Supplies')
,('Fast Food & Carry Out')
,('Grocery')
,('Restaurants')
,('Health & Medicine')
,('Acupuncture')
,('Assisted Living & Home Health Care')
,('Audiologist')
,('Chiropractic')
,('Clinics & Medical Centers')
,('Dental')
,('Diet & Nutrition')
,('Laboratory')
,('Massage Therapy')
,('Mental Health')
,('Nurse')
,('Optical')
,('Pharmacy')
,('Physical Therapy')
,('Physicians & Assistants')
,('Podiatry')
,('Social Worker')
,('Animal Hospital')
,('Veterinary & Animal Surgeons')
,('Home & Garden')
,('Antiques & Collectibles')
,('Cleaning')
,('Crafts')
,('Flower Shops')
,('Home Furnishings')
,('Home Goods')
,('Home Improvements & Repairs')
,('Landscape & Lawn Service')
,('Pest Control')
,('Pool Supplies & Service')
,('Security System & Services')
,('Legal & Financial')
,('Accountants')
,('Attorneys')
,('Financial Institutions')
,('Financial Services')
,('Insurance')
,('Other Legal')
,('Manufacturing')
,('Distribution')
,('Wholesale')
,('Merchants')
,('Cards & Gifts')
,('Clothing & Accessories')
,('Department Stores')
,('Sporting Goods')
,('General')
,('Jewelry')
,('Shoes')
,('Miscellaneous')
,('Civic Groups')
,('Funeral Service Providers & Cemetaries')
,('Miscellaneous')
,('Utility Companies')
,('Personal Care & Services')
,('Animal Care & Supplies')
,('Barber & Beauty Salons')
,('Beauty Supplies')
,('Dry Cleaners & Laundromats')
,('Exercise & Fitness')
,('Massage & Body Works')
,('Nail Salons')
,('Shoe Repairs')
,('Tailors')
,('Real Estate')
,('Agencies & Brokerage')
,('Agents & Brokers')
,('Apartment & Home Rental')
,('Mortgage Broker & Lender')
,('Property Management')
,('Title Company')
,('Travel & Transportation')
,('Hotel')
,('Moving & Storage')
,('Packaging & Shipping')
,('Transportation')
,('Travel & Tourism');

INSERT INTO public.offercriteriontypes(offercriteriontype, offercriterionsql) VALUES ('>', '>'), ('<', '<'), ('=', '='), ('Exists', 'is not null');

SELECT public.createoffer(
    1,
    10,
    '2016-03-15 12:00:00',
    '2016-04-15 12:00:00',
    3,
    4,
    0,
    'BMW is looking for the next new drivers for our i8 model - the most progressive sports car. The BMW i8 is ready to revolutionise its vehicle class.',
    'Auto Dealers – New',
    'Salary,Credit Score',
    'Home Address'
);


SELECT public.createoffer(
    2,
    5,
    '2016-04-15 12:00:00',
    '2016-05-15 12:00:00',
    2.5,
    1,
    1,
    'Get cheap flights.',
    'Travel & Transportation,Transportation',
    'Salary,Favourite Sports Teams',
    'Home Address'
);

INSERT INTO public.demo_message(offerid, message, timestamp)
VALUES
    (1, 'Please call 1-XXX-XXX-XXXX to redeem your coupon.', '2016-03-12 21:00:05.539000');

