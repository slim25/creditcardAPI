db = db.getSiblingDB('creditcardAPI');

db.createUser({
  user: "apiuser",
  pwd: "myTestPass1111",
  roles: [
    { role: "readWrite", db: "creditcardAPI" }
  ]
});