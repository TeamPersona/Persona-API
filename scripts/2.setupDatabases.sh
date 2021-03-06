
sh setupCassandra.sh;

psql -h localhost -d persona -a -f postgresClean.sql;
psql -h localhost -d persona -a -f postgresSchemas.sql;
psql -h localhost -d persona -a -f postgresData.sql;

TOKEN=$(curl -silent -X POST --data-urlencode "given_name=Bob" --data-urlencode "family_name=Jones" --data-urlencode "email=bob@persona.com" --data-urlencode "phone_number=5198888888" --data-urlencode "password=password" --data-urlencode "client_id=11205499856203749022.apps.uwpersona.com" localhost:9000/account | grep access_token | cut -d':' -f2 | sed 's/.$//';);

echo ${TOKEN//\"/};

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Date of Birth",
    "data": {
    	"Day":"28",
    	"Month":"October",
    	"Year":"1986"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Name",
    "data": {
    	"First Name":"Bob",
    	"Last Name":"Jones"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Home Address",
    "data": {
    	"Street Address":"200 University Ave",
    	"City":"Waterloo",
    	"Province":"Ontario",
    	"Postal Code":"N2L 3G1"
    }
}' localhost:9000/bank;


curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Email Address",
    "data": {
    	"Email Address":"bob@persona.com"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Phone Number",
    "data": {
    	"Phone Number":"(519)-888-8888"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Allow Contact",
    "data": {
    	"Allow Contact":"True"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Basic",
    "subcategory": "Gender",
    "data": {
    	"Gender":"Male"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Entertainment",
    "subcategory": "Movie Interests",
    "data": {
    	"Movie Category":"Romantic Comedy"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Entertainment",
    "subcategory": "Favourite Sports Teams",
    "data": {
    	"Favourite Baseball Team":"Kansas City Royals"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Financial",
    "subcategory": "Salary",
    "data": {
    	"Salary":"230000"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Health & Fitness",
    "subcategory": "Weight",
    "data": {
    	"Weight":"235"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Health & Fitness",
    "subcategory": "Height",
    "data": {
    	"Height":"6 ft. 2 in."
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Personal",
    "subcategory": "Current Employer",
    "data": {
    	"Current Employer":"University of Waterloo"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Personal",
    "subcategory": "Workplace OS",
    "data": {
    	"Operating System":"Linux"
    }
}' localhost:9000/bank;

curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer ${TOKEN//\"/}" -d '{
    "category": "Personal",
    "subcategory": "Marital Status",
    "data": {
    	"Marital Status":"Single"
    }
}' localhost:9000/bank;

psql -h localhost -d persona -c 'INSERT INTO public.offerparticipation(offerid, userid) VALUES (5, 1), (6,1),(7,1);';
