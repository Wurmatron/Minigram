/*
    Navbar component, should be present in every other component, but LoginPage as Login Page just needs the text from the bar
*/

class Navbar extends React.Component{
    constructor(props) {
        super(props);
    }

    render(){
        return(
        <div className="sticky-top">
            <nav className="navbar navbar-light bg-light border-bottom border-dark">
                <span className="navbar-text headerText text-dark">Minigram </span>
            </nav>
        </div>
        )
    }
}

export default Navbar