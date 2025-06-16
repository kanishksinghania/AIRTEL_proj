export async function registerUser(user) {
  const response = await fetch('http://192.168.1.12:8080/AirtelAuth/main', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
          action: 'register',
          fname: user.fname,
          lname: user.lname,
          email: user.email,
          password: user.password,
          age: user.age,
          phone: user.phone,
          role: user.role,
          address: user.address
      })
  });
  return response.json();
}


export const loginUser = async (userData) => {
  const response = await fetch("http://192.168.1.12:8080/AirtelAuth/main", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      action: "login",
      email: userData.email,
      password: userData.password,
    }),
  });

  return response.json(); 
};



export const fetchAdmins = async () => {
  const response = await fetch("http://192.168.1.12:8080/AirtelAuth/main?action=admins");
  return response.json();
};

export const fetchUsers = async () => {
  const response = await fetch("http://192.168.1.12:8080/AirtelAuth/main?action=users");
  return response.json();
};

export const fetchAllUsers = async () => {
  const response = await fetch("http://192.168.1.12:8080/AirtelAuth/main?action=all-users");
  return response.json();
};


