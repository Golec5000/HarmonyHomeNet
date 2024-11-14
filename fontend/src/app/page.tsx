import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LoginForm from '@/app/(pages)/login/page';
import Welcome from '@/app/(pages)/welcome-home/page';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginForm />} />
                <Route path="/welcome-home" element={<Welcome />} />
                <Route path="/*" element={<Navigate to="/welcome-home" />} />
            </Routes>
        </Router>
    );
};

export default App;